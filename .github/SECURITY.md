# Security Policy

## Reporting a Vulnerability

If you discover a security vulnerability in SlothBoot, please report it responsibly.

**Do NOT open a public GitHub issue for security vulnerabilities.**

Instead, please send an email to the maintainers with:

1. A description of the vulnerability
2. Steps to reproduce the issue
3. The potential impact
4. Any suggested fixes (if applicable)

We will acknowledge receipt within 48 hours and aim to provide a fix or mitigation plan within 7 days.

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

## Security Best Practices

When using SlothBoot in production:

- Always configure `sloth.mybatis.encrypt-key` with a strong, unique key
- Enable HTTPS in production environments
- Configure proper CORS policies via `sloth.web.cors`
- Use Sa-Token's black/white list for access control
- Keep dependencies up to date via Dependabot PRs
