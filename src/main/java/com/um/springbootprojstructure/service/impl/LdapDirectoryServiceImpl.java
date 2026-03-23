package com.um.springbootprojstructure.service.impl;

import com.um.springbootprojstructure.dto.LdapUserResponse;
import com.um.springbootprojstructure.service.LdapDirectoryService;
import com.um.springbootprojstructure.service.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.*;

@Service
public class LdapDirectoryServiceImpl implements LdapDirectoryService {

    private final LdapTemplate ldapTemplate;
    private final String baseDn;
    private final String userSearchBase;
    private final String userSearchFilterTemplate;
    private final List<String> attributeAllowList;

    public LdapDirectoryServiceImpl(
            LdapTemplate ldapTemplate,
            @Value("${app.ldap.base-dn}") String baseDn,
            @Value("${app.ldap.user-search-base:}") String userSearchBase,
            @Value("${app.ldap.user-search-filter:(uid={0})}") String userSearchFilterTemplate,
            @Value("${app.ldap.user-attributes:}") String attributesCsv
    ) {
        this.ldapTemplate = ldapTemplate;
        this.baseDn = baseDn;
        this.userSearchBase = userSearchBase;
        this.userSearchFilterTemplate = userSearchFilterTemplate;

        if (attributesCsv == null || attributesCsv.isBlank()) {
            this.attributeAllowList = List.of();
        } else {
            this.attributeAllowList = Arrays.stream(attributesCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();
        }
    }

    @Override
    public List<LdapUserResponse> searchUser(String dc, String username) {
        if (dc == null || dc.isBlank()) {
            throw new BadRequestException("MISSING_DC", "dc query parameter is required");
        }
        if (username == null || username.isBlank()) {
            throw new BadRequestException("MISSING_USERNAME", "username query parameter is required");
        }

        // Construct a base that includes requested domain component.
        // Example:
        //   app.ldap.base-dn = dc=example,dc=com
        //   dc param "corp" => dc=corp,dc=example,dc=com
        String effectiveBase = "dc=" + escapeRdnValue(dc) + "," + baseDn;

        // Search base under effectiveBase (e.g. ou=people)
        String searchBase = (userSearchBase == null || userSearchBase.isBlank())
                ? effectiveBase
                : userSearchBase + "," + effectiveBase;

        // Build filter safely.
        // Prefer a fixed filter like (uid={0}) from config; we'll substitute and escape the value.
        String filter = userSearchFilterTemplate.replace("{0}", escapeFilterValue(username));

        return ldapTemplate.search(
                searchBase,
                filter,
                (AttributesMapper<LdapUserResponse>) attrs -> mapToResponse(attrs)
        );
    }

    private LdapUserResponse mapToResponse(Attributes attrs) throws javax.naming.NamingException {
        Map<String, Object> map = new LinkedHashMap<>();

        NamingEnumeration<? extends Attribute> all = attrs.getAll();
        while (all.hasMore()) {
            Attribute a = all.next();
            String id = a.getID();

            if (!attributeAllowList.isEmpty() && !attributeAllowList.contains(id)) {
                continue;
            }

            List<Object> values = new ArrayList<>();
            NamingEnumeration<?> ve = a.getAll();
            while (ve.hasMore()) values.add(ve.next());

            map.put(id, values.size() == 1 ? values.get(0) : values);
        }

        // DN isn't directly available from AttributesMapper; to include DN, use ContextMapper.
        // We'll keep dn null in this mapper approach unless you request DN specifically.
        return new LdapUserResponse(null, map);
    }

    // Minimal escaping helpers (avoid injection)
    private String escapeFilterValue(String value) {
        return value
                .replace("\\", "\\5c")
                .replace("*", "\\2a")
                .replace("(", "\\28")
                .replace(")", "\\29")
                .replace("\u0000", "\\00");
    }

    private String escapeRdnValue(String value) {
        // simplistic; sufficient for dc values like "corp"
        return value.replace(",", "\\,").replace("=", "\\=");
    }
}
