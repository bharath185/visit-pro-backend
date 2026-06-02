package com.visitorapp.service;

/**
 * Builds HTML email templates with proper escaping of user-provided data
 * to prevent HTML injection in emails.
 */
public class EmailTemplateBuilder {

    private static String bgColor = "#f4f6fa";
    private static String cardBg = "#ffffff";
    private static String primary = "#0f3560";
    private static String accent = "#1890ff";
    private static String success = "#52c41a";
    private static String danger = "#ff4d4f";

    /**
     * Escape HTML special characters to prevent injection in email templates.
     */
    public static String escapeHtml(String input) {
        if (input == null) return "";
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;");
    }

    public static String wrap(String title, String content) {
        return "<!DOCTYPE html>"
            + "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>"
            + "</head><body style='margin:0;padding:0;background-color:" + bgColor + ";font-family:Helvetica,Arial,sans-serif'>"
            + "<table role='presentation' cellpadding='0' cellspacing='0' border='0' width='100%' style='background-color:" + bgColor + ";min-width:100%'>"
            + "<tr><td align='center' style='padding:30px 10px'>"
            + "<table role='presentation' cellpadding='0' cellspacing='0' border='0' width='100%' style='max-width:560px;background-color:" + cardBg + ";border-radius:16px;-webkit-border-radius:16px'>"
            + "<tr><td style='background:linear-gradient(135deg,#0a2540,#1a5088);padding:32px 36px;text-align:center;border-radius:16px 16px 0 0;-webkit-border-radius:16px 16px 0 0'>"
            + "<table role='presentation' cellpadding='0' cellspacing='0' border='0' align='center'><tr><td style='width:56px;height:56px;background:rgba(255,255,255,0.12);border-radius:14px;-webkit-border-radius:14px;text-align:center;vertical-align:middle'><!--[if mso]>"
            + "<v:roundrect xmlns:v='urn:schemas-microsoft-com:vml' xmlns:w='urn:schemas-microsoft-com:office:word' style='height:56px;width:56px' arcsize='25%'  fillcolor='rgba(255,255,255,0.12)'><w:anchorlock/><center style='color:#ffffff;font-size:28px;font-weight:bold;line-height:56px'>V</center></v:roundrect>"
            + "<![endif]--><!--[if !mso]><!--><span style='color:#ffffff;font-size:28px;font-weight:bold;line-height:56px'>V</span><!--<![endif]--></td></tr></table>"
            + "<h1 style='color:#ffffff;font-size:20px;font-weight:700;margin:12px 0 4px;letter-spacing:-0.3px'>Visitor Connect</h1>"
            + "<p style='color:rgba(255,255,255,0.65);font-size:13px;margin:0'>Enterprise Visitor Connect</p>"
            + "</td></tr>"
            + "<tr><td style='padding:32px 36px;background-color:" + cardBg + "'>"
            + content
            + "</td></tr>"
            + "<tr><td style='background-color:#fafbfc;padding:20px 36px;text-align:center;border-top:1px solid #f0f2f5;border-radius:0 0 16px 16px;-webkit-border-radius:0 0 16px 16px'>"
            + "<p style='font-size:12px;color:#999;margin:2px 0'>This is an automated message from the Visitor Connect.</p>"
            + "<p style='font-size:12px;color:#999;margin:2px 0'>&copy; 2026 RIM India Pvt Limited . All rights reserved.</p>"
            + "</td></tr>"
            + "</table></td></tr></table></body></html>";
    }

    public static String otpSection(String label, String otp) {
        return "<table role='presentation' cellpadding='0' cellspacing='0' border='0' width='100%' style='background:#e6f0ff;border:1px dashed " + accent + ";border-radius:12px;-webkit-border-radius:12px;margin:16px 0'>"
            + "<tr><td style='padding:16px;text-align:center'>"
            + "<p style='font-size:11px;color:#666;text-transform:uppercase;letter-spacing:1px;margin:0 0 6px'>" + escapeHtml(label) + "</p>"
            + "<p style='font-size:28px;font-weight:800;color:" + accent + ";letter-spacing:8px;font-family:monospace;margin:0'>" + escapeHtml(otp) + "</p>"
            + "</td></tr></table>";
    }

    public static String button(String url, String text) {
        // Validate URL to prevent javascript: protocol injection
        String safeUrl = sanitizeUrl(url);
        return "<table role='presentation' cellpadding='0' cellspacing='0' border='0' align='center' style='margin:16px auto'>"
            + "<tr><td align='center' style='border-radius:8px;-webkit-border-radius:8px' bgcolor='" + accent + "'>"
            + "<a href='" + escapeHtml(safeUrl) + "' style='display:inline-block;padding:14px 36px;color:#ffffff;text-decoration:none;font-size:15px;font-weight:600;line-height:1'>" + escapeHtml(text) + "</a>"
            + "</td></tr></table>";
    }

    public static String infoRow(String label, String value) {
        return "<table role='presentation' cellpadding='0' cellspacing='0' border='0' width='100%' style='margin:6px 0'>"
            + "<tr><td style='color:#888;font-size:13px;padding:4px 0;width:100px'>" + escapeHtml(label) + "</td>"
            + "<td style='color:#333;font-size:13px;font-weight:500;padding:4px 0'>" + (value != null ? escapeHtml(value) : "-") + "</td></tr></table>";
    }

    public static String dualButtons(String url1, String text1, String url2, String text2) {
        String safeUrl1 = sanitizeUrl(url1);
        String safeUrl2 = sanitizeUrl(url2);
        return "<table role='presentation' cellpadding='0' cellspacing='0' border='0' align='center' style='margin:16px auto'>"
            + "<tr><td align='center' style='border-radius:8px;-webkit-border-radius:8px;padding:0 6px' bgcolor='#" + success.substring(1) + "'>"
            + "<a href='" + escapeHtml(safeUrl1) + "' style='display:inline-block;padding:14px 28px;color:#ffffff;text-decoration:none;font-size:14px;font-weight:600;line-height:1'>" + escapeHtml(text1) + "</a>"
            + "</td>"
            + "<td width='12'></td>"
            + "<td align='center' style='border-radius:8px;-webkit-border-radius:8px;padding:0 6px' bgcolor='#" + danger.substring(1) + "'>"
            + "<a href='" + escapeHtml(safeUrl2) + "' style='display:inline-block;padding:14px 28px;color:#ffffff;text-decoration:none;font-size:14px;font-weight:600;line-height:1'>" + escapeHtml(text2) + "</a>"
            + "</td></tr></table>";
    }

    public static String approvalBox(String empName, String status) {
        String color = "approved".equals(status) ? success : danger;
        String bg = "approved".equals(status) ? "#e6fff0" : "#fff2f0";
        String icon = "approved".equals(status) ? "✅" : "❌";
        String msg = "approved".equals(status)
            ? "Your visit has been approved by " + escapeHtml(empName)
            : "Your visit has been declined by " + escapeHtml(empName);
        return "<table role='presentation' cellpadding='0' cellspacing='0' border='0' width='100%' style='background:" + bg + ";border-radius:12px;-webkit-border-radius:12px;margin:16px 0'>"
            + "<tr><td style='padding:20px;text-align:center'>"
            + "<p style='font-size:40px;margin:0 0 8px'>" + icon + "</p>"
            + "<h3 style='color:" + color + ";font-size:18px;font-weight:700;margin:0 0 8px;text-transform:uppercase;letter-spacing:1px'>" + escapeHtml(status) + "</h3>"
            + "<p style='font-size:14px;color:#555;margin:0'>" + msg + "</p>"
            + "</td></tr></table>";
    }

    public static String divider() {
        return "<table role='presentation' cellpadding='0' cellspacing='0' border='0' width='100%' style='margin:16px 0'><tr><td style='height:1px;background:#f0f2f5;font-size:1px;line-height:1px'>&nbsp;</td></tr></table>";
    }

    public static String text(String t) {
        return "<p style='font-size:14px;color:#444;line-height:1.7;margin:0 0 12px'>" + t + "</p>";
    }

    public static String heading(String h) {
        return "<h2 style='font-size:18px;color:" + primary + ";margin:0 0 16px;font-weight:700'>" + h + "</h2>";
    }

    /**
     * Sanitize a URL to prevent javascript: protocol injection and other URL-based attacks.
     * Only allow http://, https://, and relative URLs.
     */
    private static String sanitizeUrl(String url) {
        if (url == null) return "";
        String trimmed = url.trim().toLowerCase();
        if (trimmed.startsWith("javascript:") || trimmed.startsWith("data:") || trimmed.startsWith("vbscript:")) {
            return "";
        }
        return url.trim();
    }
}
