package com.vin.VinSystem.Notification.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class ContractPdfService {

    private static final Logger log = LoggerFactory.getLogger(ContractPdfService.class);

    private static final DateTimeFormatter DATE_FMT  = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DAY_FMT   = DateTimeFormatter.ofPattern("dd");
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MM");
    private static final DateTimeFormatter YEAR_FMT  = DateTimeFormatter.ofPattern("yyyy");

    // ── Font loader ───────────────────────────────────────────────────────────

    private BaseFont loadFont(String filename) {
        try {
            ClassPathResource res = new ClassPathResource("fonts/" + filename);
            try (InputStream is = res.getInputStream()) {
                byte[] bytes = is.readAllBytes();
                return BaseFont.createFont(filename, BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED, true, bytes, null);
            }
        } catch (Exception e) {
            log.warn("[PDF] Không load được font {}, fallback Helvetica", filename);
            try { return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED); }
            catch (Exception ex) { throw new RuntimeException("Font error", ex); }
        }
    }

    private Font font(BaseFont bf, float size, int style) {
        return new Font(bf, size, style, Color.BLACK);
    }
    private Font fontColor(BaseFont bf, float size, int style, Color c) {
        return new Font(bf, size, style, c);
    }

    // =========================================================
    // HỢP ĐỒNG MUA XE — gọi khi COMPLETED
    // =========================================================

    public byte[] generatePurchaseContract(
            Long   depositId,
            String customerName,
            String customerEmail,
            String carName,
            String branchName,
            BigDecimal depositAmount,
            BigDecimal onRoadTotal,
            BigDecimal remainingPaid,
            LocalDateTime completedAt) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 70, 70, 80, 80);
            PdfWriter writer = PdfWriter.getInstance(doc, baos);

            BaseFont bfR = loadFont("DejaVuSans.ttf");
            BaseFont bfB = loadFont("DejaVuSans-Bold.ttf");
            BaseFont bfI = loadFont("DejaVuSans.ttf"); // italic fallback

            writer.setPageEvent(new FooterEvent(bfR));
            doc.open();

            LocalDateTime now = completedAt != null ? completedAt : LocalDateTime.now();

            // ── TIÊU ĐỀ QUỐC GIA ─────────────────────────────────────────────
            addCenteredBold(doc, "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", bfB, 13);
            addCenteredBold(doc, "Độc lập – Tự do – Hạnh phúc", bfB, 12);
            addCenteredBold(doc, "──────────────────────", bfR, 10);
            doc.add(Chunk.NEWLINE);

            // ── TÊN HỢP ĐỒNG ─────────────────────────────────────────────────
            addCenteredBold(doc, "HỢP ĐỒNG MUA BÁN XE Ô TÔ", bfB, 14);
            doc.add(Chunk.NEWLINE);

            // ── NGÀY THÁNG ───────────────────────────────────────────────────
            Paragraph dateLine = new Paragraph(
                    "Hôm nay, ngày " + DAY_FMT.format(now) +
                    " tháng " + MONTH_FMT.format(now) +
                    " năm " + YEAR_FMT.format(now) +
                    ", tại " + safe(branchName) + ". Chúng tôi gồm:",
                    font(bfI, 11, Font.ITALIC));
            dateLine.setAlignment(Element.ALIGN_LEFT);
            doc.add(dateLine);
            doc.add(Chunk.NEWLINE);

            // ── BÊN BÁN (Bên A) ──────────────────────────────────────────────
            addUnderlineBold(doc, "Bên bán", " (Sau đây gọi tắt là ", "Bên A", ")", bfR, bfB, 11);
            addFieldLine(doc, "Tên doanh nghiệp: ", "Công ty TNHH Kinh doanh Thương mại VinFast", bfR, bfB, 11);
            addFieldLine(doc, "Chi nhánh: ",        safe(branchName), bfR, bfB, 11);
            addFieldLine(doc, "Hotline: ",           "1800 2656", bfR, bfB, 11);
            doc.add(Chunk.NEWLINE);

            // ── BÊN MUA (Bên B) ──────────────────────────────────────────────
            addUnderlineBold(doc, "Bên mua", " (Sau đây gọi tắt là ", "Bên B", ")", bfR, bfB, 11);
            addFieldLine(doc, "Họ và tên: ",    safe(customerName),  bfR, bfB, 11);
            addFieldLine(doc, "Email: ",         safe(customerEmail), bfR, bfB, 11);
            doc.add(Chunk.NEWLINE);

            Paragraph intro = new Paragraph(
                    "Hai bên đồng ý thực hiện việc mua bán xe ô tô với các thỏa thuận sau:",
                    font(bfI, 11, Font.ITALIC));
            doc.add(intro);
            doc.add(Chunk.NEWLINE);

            // ══════════════════════════════════════════════════════════════════
            // ĐIỀU 1 — XE Ô TÔ MUA BÁN
            // ══════════════════════════════════════════════════════════════════
            addArticleTitle(doc, "ĐIỀU 1", "XE Ô TÔ MUA BÁN", bfB);

            Paragraph d1 = new Paragraph(
                    "Bên A đồng ý bán và Bên B đồng ý mua một (01) xe ô tô với thông tin như sau:",
                    font(bfR, 11, Font.NORMAL));
            doc.add(d1);
            doc.add(Chunk.NEWLINE);

            // Bảng thông tin xe
            PdfPTable carTable = new PdfPTable(2);
            carTable.setWidthPercentage(85);
            carTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            carTable.setWidths(new float[]{2f, 3f});
            addCarRow(carTable, "Nhãn hiệu",    "VinFast",    bfR, bfB);
            addCarRow(carTable, "Mẫu xe",        safe(carName), bfR, bfB);
            addCarRow(carTable, "Mã đặt cọc",    "#" + depositId, bfR, bfB);
            addCarRow(carTable, "Ngày giao dịch", DATE_FMT.format(now), bfR, bfB);
            doc.add(carTable);
            doc.add(Chunk.NEWLINE);

            // ══════════════════════════════════════════════════════════════════
            // ĐIỀU 2 — GIÁ MUA BÁN VÀ PHƯƠNG THỨC THANH TOÁN
            // ══════════════════════════════════════════════════════════════════
            addArticleTitle(doc, "ĐIỀU 2", "GIÁ MUA BÁN VÀ PHƯƠNG THỨC THANH TOÁN", bfB);

            addBodyText(doc, "Giá mua bán chiếc xe nêu trên theo thỏa thuận giữa hai bên:", bfR, 11);
            doc.add(Chunk.NEWLINE);

            // Bảng thanh toán chi tiết
            PdfPTable payTable = new PdfPTable(2);
            payTable.setWidthPercentage(90);
            payTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            payTable.setWidths(new float[]{3f, 2f});
            addPaymentRow(payTable, "Tiền đặt cọc đã thanh toán",       fmt(depositAmount),  bfR, bfB, false);
            addPaymentRow(payTable, "Số tiền còn lại phải thanh toán",  fmt(remainingPaid),  bfR, bfB, false);
            addPaymentRow(payTable, "TỔNG GIÁ TRỊ HỢP ĐỒNG (Chi phí lăn bánh)", fmt(onRoadTotal), bfR, bfB, true);
            doc.add(payTable);
            doc.add(Chunk.NEWLINE);

            addBodyText(doc,
                    "Bằng chữ: " + amountToWords(onRoadTotal) + ".",
                    bfI, 11);
            addBodyText(doc,
                    "Việc thanh toán số tiền nêu trên do hai bên tự thực hiện và chịu trách nhiệm trước pháp luật.",
                    bfR, 11);
            doc.add(Chunk.NEWLINE);

            // ══════════════════════════════════════════════════════════════════
            // ĐIỀU 3 — PHƯƠNG THỨC GIAO NHẬN XE
            // ══════════════════════════════════════════════════════════════════
            addArticleTitle(doc, "ĐIỀU 3", "PHƯƠNG THỨC GIAO NHẬN XE", bfB);
            addBodyText(doc,
                    "Việc giao nhận tiền, giao nhận xe và các giấy tờ liên quan do hai bên tự thực hiện " +
                    "và chịu trách nhiệm trước pháp luật. Bên A có trách nhiệm bàn giao xe tại chi nhánh " +
                    safe(branchName) + " sau khi Bên B hoàn thành nghĩa vụ thanh toán theo Điều 2.",
                    bfR, 11);
            doc.add(Chunk.NEWLINE);

            // ══════════════════════════════════════════════════════════════════
            // ĐIỀU 4 — QUYỀN SỞ HỮU ĐỐI VỚI XE MUA BÁN
            // ══════════════════════════════════════════════════════════════════
            addArticleTitle(doc, "ĐIỀU 4", "QUYỀN SỞ HỮU ĐỐI VỚI XE MUA BÁN", bfB);
            addBodyText(doc,
                    "Bên B có trách nhiệm thực hiện việc đăng ký quyền sở hữu đối với xe nêu trên tại cơ quan " +
                    "có thẩm quyền. Quyền sở hữu đối với xe nêu tại Điều 1 đã được chuyển cho Bên B, kể từ " +
                    "thời điểm thực hiện xong thủ tục đăng ký quyền sở hữu xe.",
                    bfR, 11);
            doc.add(Chunk.NEWLINE);

            // ══════════════════════════════════════════════════════════════════
            // ĐIỀU 5 — VIỆC NỘP THUẾ, PHÍ
            // ══════════════════════════════════════════════════════════════════
            addArticleTitle(doc, "ĐIỀU 5", "VIỆC NỘP THUẾ, PHÍ", bfB);
            addBodyText(doc,
                    "Thuế, phí liên quan đến việc mua bán chiếc xe nêu tại Điều 1 theo Hợp đồng này do Bên B " +
                    "chịu trách nhiệm nộp theo quy định của pháp luật hiện hành.",
                    bfR, 11);
            doc.add(Chunk.NEWLINE);

            // ══════════════════════════════════════════════════════════════════
            // ĐIỀU 6 — BẢO HÀNH VÀ DỊCH VỤ SAU BÁN HÀNG
            // ══════════════════════════════════════════════════════════════════
            addArticleTitle(doc, "ĐIỀU 6", "BẢO HÀNH VÀ DỊCH VỤ SAU BÁN HÀNG", bfB);
            addBodyText(doc,
                    "Xe được bảo hành theo chính sách bảo hành hiện hành của VinFast tại thời điểm bàn giao. " +
                    "Mọi thông tin chi tiết về chính sách bảo hành được quy định trong Phiếu bảo hành kèm theo xe. " +
                    "Hotline hỗ trợ: 1800 2656 (miễn phí, 24/7).",
                    bfR, 11);
            doc.add(Chunk.NEWLINE);

            // ══════════════════════════════════════════════════════════════════
            // ĐIỀU 7 — PHƯƠNG THỨC GIẢI QUYẾT TRANH CHẤP
            // ══════════════════════════════════════════════════════════════════
            addArticleTitle(doc, "ĐIỀU 7", "PHƯƠNG THỨC GIẢI QUYẾT TRANH CHẤP", bfB);
            addBodyText(doc,
                    "Trong quá trình thực hiện Hợp đồng mà phát sinh tranh chấp, các bên cùng nhau thương lượng, " +
                    "giải quyết trên nguyên tắc tôn trọng quyền lợi của nhau; trong trường hợp không giải quyết được " +
                    "thì một trong hai bên có quyền khởi kiện để yêu cầu Tòa án có thẩm quyền giải quyết theo quy " +
                    "định của pháp luật.",
                    bfR, 11);
            doc.add(Chunk.NEWLINE);

            // ══════════════════════════════════════════════════════════════════
            // ĐIỀU 8 — CAM ĐOAN CỦA CÁC BÊN
            // ══════════════════════════════════════════════════════════════════
            addArticleTitle(doc, "ĐIỀU 8", "CAM ĐOAN CỦA CÁC BÊN", bfB);
            addBodyText(doc, "Bên A và Bên B chịu trách nhiệm trước pháp luật về những lời cam đoan sau đây:", bfR, 11);

            addBoldItem(doc, "1. Bên A cam đoan:", bfB, bfR, 11, new String[]{
                "Những thông tin về doanh nghiệp ghi trong Hợp đồng này là đúng sự thật;",
                "Tài sản mua bán không có tranh chấp, không bị cơ quan nhà nước có thẩm quyền xử lý;",
                "Việc giao kết hợp đồng này hoàn toàn tự nguyện, không bị lừa dối hoặc ép buộc."
            });

            addBoldItem(doc, "2. Bên B cam đoan:", bfB, bfR, 11, new String[]{
                "Những thông tin về nhân thân mà Bên B cung cấp ghi trong hợp đồng là đúng sự thật;",
                "Bên B đã xem xét kỹ, biết rõ về chiếc xe nêu tại Điều 1 của Hợp đồng này;",
                "Việc giao kết Hợp đồng này hoàn toàn tự nguyện, không bị lừa dối, không bị ép buộc."
            });

            addBoldItem(doc, "3. Hai bên cam đoan:", bfB, bfR, 11, new String[]{
                "Đảm bảo tính chính xác, trung thực và hoàn toàn chịu trách nhiệm trước pháp luật;",
                "Thực hiện đúng và đầy đủ tất cả các thỏa thuận đã ghi trong Hợp đồng này."
            });
            doc.add(Chunk.NEWLINE);

            // ══════════════════════════════════════════════════════════════════
            // ĐIỀU 9 — ĐIỀU KHOẢN CUỐI CÙNG
            // ══════════════════════════════════════════════════════════════════
            addArticleTitle(doc, "ĐIỀU 9", "ĐIỀU KHOẢN CUỐI CÙNG", bfB);
            addNumberedItems(doc, bfR, 11, new String[]{
                "Hai bên công nhận đã hiểu rõ quyền, nghĩa vụ và lợi ích hợp pháp của mình, ý nghĩa và hậu quả pháp lý của việc giao kết Hợp đồng này.",
                "Hai bên đã tự đọc nguyên văn, đầy đủ các trang của bản Hợp đồng này và không yêu cầu chỉnh sửa, thêm, bớt bất cứ thông tin gì trong bản hợp đồng này.",
                "Hợp đồng này có hiệu lực kể từ thời điểm các bên ký vào Hợp đồng này."
            });
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);

            // ── CHỮ KÝ ───────────────────────────────────────────────────────
            addSignatureBlock(doc, customerName, branchName, now, bfR, bfB);

            doc.close();
            log.info("[PDF] generatePurchaseContract depositId={} size={}B", depositId, baos.size());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("[PDF] generatePurchaseContract failed depositId={} err={}", depositId, e.getMessage(), e);
            throw new RuntimeException("Tạo PDF hợp đồng thất bại: " + e.getMessage(), e);
        }
    }

    // =========================================================
    // BIÊN LAI THANH TOÁN — gọi khi FULL_PAYMENT COMPLETED
    // =========================================================

    public byte[] generatePaymentConfirmation(
            Long depositId,
            Long paymentId,
            String customerName,
            String carName,
            BigDecimal amount,
            String paymentMethod,
            String txnRef,
            LocalDateTime paidAt) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 70, 70, 80, 80);
            PdfWriter writer = PdfWriter.getInstance(doc, baos);

            BaseFont bfR = loadFont("DejaVuSans.ttf");
            BaseFont bfB = loadFont("DejaVuSans-Bold.ttf");
            writer.setPageEvent(new FooterEvent(bfR));
            doc.open();

            LocalDateTime now = paidAt != null ? paidAt : LocalDateTime.now();

            addCenteredBold(doc, "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", bfB, 13);
            addCenteredBold(doc, "Độc lập – Tự do – Hạnh phúc", bfB, 12);
            addCenteredBold(doc, "──────────────────────", bfR, 10);
            doc.add(Chunk.NEWLINE);
            addCenteredBold(doc, "BIÊN LAI XÁC NHẬN THANH TOÁN", bfB, 14);
            doc.add(Chunk.NEWLINE);

            addFieldLine(doc, "Số biên lai: ", "TT-VF-" + String.format("%06d", paymentId), bfR, bfB, 11);
            addFieldLine(doc, "Ngày lập: ", DATE_FMT.format(now), bfR, bfB, 11);
            doc.add(Chunk.NEWLINE);

            addArticleTitle(doc, "I.", "THÔNG TIN KHÁCH HÀNG", bfB);
            addFieldLine(doc, "Họ và tên: ",    safe(customerName), bfR, bfB, 11);
            addFieldLine(doc, "Mã đặt cọc: ",   "#" + depositId,    bfR, bfB, 11);
            doc.add(Chunk.NEWLINE);

            addArticleTitle(doc, "II.", "THÔNG TIN THANH TOÁN", bfB);
            addFieldLine(doc, "Xe: ",            safe(carName),           bfR, bfB, 11);
            addFieldLine(doc, "Phương thức: ",   fmtMethod(paymentMethod), bfR, bfB, 11);
            addFieldLine(doc, "Mã giao dịch: ",  paymentId != null ? "#" + paymentId : "—", bfR, bfB, 11);
            addFieldLine(doc, "Mã VNPay: ",      safe(txnRef),            bfR, bfB, 11);
            addFieldLine(doc, "Thời gian: ",     DATE_FMT.format(now),    bfR, bfB, 11);
            doc.add(Chunk.NEWLINE);

            addArticleTitle(doc, "III.", "SỐ TIỀN THANH TOÁN", bfB);

            PdfPTable amtTable = new PdfPTable(2);
            amtTable.setWidthPercentage(70);
            amtTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            amtTable.setWidths(new float[]{3f, 2f});
            addPaymentRow(amtTable, "Số tiền thanh toán", fmt(amount), bfR, bfB, true);
            doc.add(amtTable);
            doc.add(Chunk.NEWLINE);

            addBodyText(doc, "Bằng chữ: " + amountToWords(amount) + ".", bfR, 11);
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);

            addBodyText(doc,
                    "Biên lai này xác nhận Bên mua đã hoàn thành nghĩa vụ thanh toán phần còn lại cho xe " +
                    safe(carName) + ". VinFast Vietnam xác nhận đã nhận đủ số tiền nêu trên.",
                    bfR, 11);
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);

            addSignatureBlock(doc, customerName, "VinFast Vietnam", now, bfR, bfB);

            doc.close();
            log.info("[PDF] generatePaymentConfirmation paymentId={} size={}B", paymentId, baos.size());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("[PDF] generatePaymentConfirmation failed paymentId={} err={}", paymentId, e.getMessage(), e);
            throw new RuntimeException("Tạo PDF biên lai thất bại: " + e.getMessage(), e);
        }
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    private void addCenteredBold(Document doc, String text, BaseFont bf, float size) throws DocumentException {
        Paragraph p = new Paragraph(text, font(bf, size, Font.BOLD));
        p.setAlignment(Element.ALIGN_CENTER);
        doc.add(p);
    }

    private void addArticleTitle(Document doc, String num, String title, BaseFont bfB) throws DocumentException {
        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_CENTER);
        p.add(new Chunk(num + "\n", font(bfB, 12, Font.BOLD)));
        p.add(new Chunk(title, font(bfB, 12, Font.BOLD)));
        p.setSpacingBefore(6);
        p.setSpacingAfter(4);
        doc.add(p);
    }

    private void addUnderlineBold(Document doc,
                                   String boldPart, String normalPart,
                                   String boldPart2, String normalEnd,
                                   BaseFont bfR, BaseFont bfB, float size) throws DocumentException {
        Paragraph p = new Paragraph();
        Chunk c1 = new Chunk(boldPart, font(bfB, size, Font.BOLD));
        c1.setUnderline(0.5f, -2f);
        p.add(c1);
        p.add(new Chunk(normalPart, font(bfR, size, Font.ITALIC)));
        Chunk c2 = new Chunk(boldPart2, font(bfB, size, Font.BOLD | Font.ITALIC));
        p.add(c2);
        p.add(new Chunk(normalEnd, font(bfR, size, Font.ITALIC)));
        p.setSpacingBefore(4);
        doc.add(p);
    }

    private void addFieldLine(Document doc, String label, String value,
                               BaseFont bfR, BaseFont bfB, float size) throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label, font(bfB, size, Font.BOLD)));
        p.add(new Chunk(value, font(bfR, size, Font.NORMAL)));
        p.setIndentationLeft(20);
        p.setSpacingBefore(2);
        doc.add(p);
    }

    private void addBodyText(Document doc, String text, BaseFont bf, float size) throws DocumentException {
        Paragraph p = new Paragraph(text, font(bf, size, Font.NORMAL));
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        p.setFirstLineIndent(20);
        p.setSpacingBefore(2);
        doc.add(p);
    }

    private void addBoldItem(Document doc, String boldLabel, BaseFont bfB, BaseFont bfR,
                              float size, String[] items) throws DocumentException {
        Paragraph header = new Paragraph(boldLabel, font(bfB, size, Font.BOLD));
        header.setSpacingBefore(4);
        doc.add(header);
        for (String item : items) {
            Paragraph p = new Paragraph("- " + item, font(bfR, size, Font.NORMAL));
            p.setIndentationLeft(15);
            p.setSpacingBefore(2);
            doc.add(p);
        }
    }

    private void addNumberedItems(Document doc, BaseFont bfR, float size, String[] items) throws DocumentException {
        for (int i = 0; i < items.length; i++) {
            Paragraph p = new Paragraph((i + 1) + ". " + items[i], font(bfR, size, Font.NORMAL));
            p.setFirstLineIndent(0);
            p.setSpacingBefore(3);
            doc.add(p);
        }
    }

    private void addCarRow(PdfPTable t, String label, String value, BaseFont bfR, BaseFont bfB) {
        PdfPCell l = new PdfPCell(new Phrase(label + "  :", font(bfB, 11, Font.BOLD)));
        l.setBorder(Rectangle.NO_BORDER); l.setPadding(3); l.setPaddingLeft(20);
        PdfPCell v = new PdfPCell(new Phrase(value, font(bfR, 11, Font.NORMAL)));
        v.setBorder(Rectangle.NO_BORDER); v.setPadding(3);
        t.addCell(l); t.addCell(v);
    }

    private void addPaymentRow(PdfPTable t, String label, String value,
                                BaseFont bfR, BaseFont bfB, boolean highlight) {
        Color bg = highlight ? new Color(235, 245, 255) : Color.WHITE;
        Font lf = highlight ? font(bfB, 11, Font.BOLD) : font(bfB, 11, Font.NORMAL);
        Font vf = highlight ? font(bfB, 11, Font.BOLD) : font(bfR, 11, Font.NORMAL);

        PdfPCell l = new PdfPCell(new Phrase(label, lf));
        l.setBackgroundColor(bg); l.setPadding(6); l.setBorderColor(new Color(200, 200, 200));
        PdfPCell v = new PdfPCell(new Phrase(value, vf));
        v.setBackgroundColor(bg); v.setPadding(6); v.setHorizontalAlignment(Element.ALIGN_RIGHT);
        v.setBorderColor(new Color(200, 200, 200));
        t.addCell(l); t.addCell(v);
    }

    private void addSignatureBlock(Document doc, String customerName,
                                    String sellerName, LocalDateTime now,
                                    BaseFont bfR, BaseFont bfB) throws DocumentException {
        // Địa điểm + ngày ký
        Paragraph dateLine = new Paragraph(
                "Làm tại ............., ngày " + DAY_FMT.format(now) +
                " tháng " + MONTH_FMT.format(now) + " năm " + YEAR_FMT.format(now),
                font(bfR, 11, Font.ITALIC));
        dateLine.setAlignment(Element.ALIGN_RIGHT);
        doc.add(dateLine);
        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);

        PdfPTable sig = new PdfPTable(2);
        sig.setWidthPercentage(100);

        // Bên bán
        PdfPCell seller = new PdfPCell();
        seller.setBorder(Rectangle.NO_BORDER);
        seller.setHorizontalAlignment(Element.ALIGN_CENTER);
        seller.addElement(center("BÊN BÁN", bfB, 12));
        seller.addElement(center("(Ký, ghi rõ họ tên)", bfR, 10));
        seller.addElement(new Phrase("\n\n\n\n\n"));
        seller.addElement(center(safe(sellerName), bfB, 11));
        sig.addCell(seller);

        // Bên mua
        PdfPCell buyer = new PdfPCell();
        buyer.setBorder(Rectangle.NO_BORDER);
        buyer.setHorizontalAlignment(Element.ALIGN_CENTER);
        buyer.addElement(center("BÊN MUA", bfB, 12));
        buyer.addElement(center("(Ký, ghi rõ họ tên)", bfR, 10));
        buyer.addElement(new Phrase("\n\n\n\n\n"));
        buyer.addElement(center(safe(customerName), bfB, 11));
        sig.addCell(buyer);

        doc.add(sig);
    }

    private Paragraph center(String text, BaseFont bf, float size) {
        Paragraph p = new Paragraph(text, font(bf, size, Font.NORMAL));
        p.setAlignment(Element.ALIGN_CENTER);
        return p;
    }

    /** Chuyển số tiền sang chữ đơn giản (chỉ đơn vị tỷ/triệu/nghìn) */
    private String amountToWords(BigDecimal amount) {
        if (amount == null) return "Không đồng";
        long val = amount.longValue();
        if (val == 0) return "Không đồng";

        StringBuilder sb = new StringBuilder();
        long ty     = val / 1_000_000_000L;
        long trieu  = (val % 1_000_000_000L) / 1_000_000L;
        long nghin  = (val % 1_000_000L) / 1_000L;
        long donvi  = val % 1_000L;

        if (ty    > 0) sb.append(ty).append(" tỷ ");
        if (trieu > 0) sb.append(trieu).append(" triệu ");
        if (nghin > 0) sb.append(nghin).append(" nghìn ");
        if (donvi > 0) sb.append(donvi);
        sb.append(" đồng");
        return sb.toString().trim();
    }

    private String fmt(BigDecimal v) {
        if (v == null) return "0 đ";
        return NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(v) + " đ";
    }

    private String fmtMethod(String m) {
        if (m == null) return "—";
        return switch (m.toUpperCase()) {
            case "CASH"          -> "Tiền mặt";
            case "BANK_TRANSFER" -> "Chuyển khoản ngân hàng";
            case "VNPAY"         -> "VNPay";
            default              -> m;
        };
    }

    private String safe(String s) { return s != null && !s.isBlank() ? s : "—"; }

    // ── Footer ───────────────────────────────────────────────────────────────

    private static class FooterEvent extends PdfPageEventHelper {
        private final BaseFont bf;
        private final String   label;
        FooterEvent(BaseFont bf) { this.bf = bf; this.label = "Hợp đồng mua bán xe ô tô"; }
        FooterEvent(BaseFont bf, String label) { this.bf = bf; this.label = label; }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Font f = new Font(bf, 8, Font.NORMAL, new Color(150, 150, 150));
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    new Phrase("VinFast Vietnam | " + label + " | Trang " + writer.getPageNumber(), f),
                    document.getPageSize().getWidth() / 2, 30, 0);
            cb.setColorStroke(new Color(200, 200, 200));
            cb.setLineWidth(0.5f);
            cb.moveTo(document.left(), 42);
            cb.lineTo(document.right(), 42);
            cb.stroke();
        }
    }

    // =========================================================
    // HỢP ĐỒNG ĐẶT CỌC — gọi ngay khi deposit APPROVED
    // =========================================================

    /**
     * Tạo PDF hợp đồng đặt cọc ngay sau khi khách hàng thanh toán cọc thành công.
     *
     * Chính sách hủy: trong 7 ngày làm việc kể từ ngày đặt cọc được hoàn tiền 100%.
     * Sau 7 ngày làm việc mà không nhận xe → mất cọc.
     */
    public byte[] generateDepositContract(
            Long depositId,
            String customerName,
            String customerEmail,
            String customerPhone,
            String carName,
            String branchName,
            String branchHotline,
            BigDecimal depositAmount,
            BigDecimal onRoadTotal,
            LocalDateTime depositDate) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 70, 70, 80, 80);
            PdfWriter writer = PdfWriter.getInstance(doc, baos);

            BaseFont bfR = loadFont("DejaVuSans.ttf");
            BaseFont bfB = loadFont("DejaVuSans-Bold.ttf");
            writer.setPageEvent(new FooterEvent(bfR, "Hợp đồng đặt cọc"));
            doc.open();

            LocalDateTime now = depositDate != null ? depositDate : LocalDateTime.now();
            String hotline = (branchHotline != null && !branchHotline.isBlank()) ? branchHotline : "1800 2656";

            // ── TIÊU ĐỀ QUỐC GIA ─────────────────────────────────────────────
            addCenteredBold(doc, "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", bfB, 13);
            addCenteredBold(doc, "Độc lập – Tự do – Hạnh phúc", bfB, 12);
            addCenteredBold(doc, "──────────────────────", bfR, 10);
            doc.add(Chunk.NEWLINE);

            addCenteredBold(doc, "HỢP ĐỒNG ĐẶT CỌC MUA BÁN XE Ô TÔ", bfB, 14);
            addCenteredBold(doc, "Số: DC-VF-" + String.format("%06d", depositId), bfR, 11);
            doc.add(Chunk.NEWLINE);

            // ── NGÀY THÁNG ───────────────────────────────────────────────────
            Paragraph dateLine = new Paragraph(
                    "Hôm nay, ngày " + DAY_FMT.format(now) +
                    " tháng " + MONTH_FMT.format(now) +
                    " năm " + YEAR_FMT.format(now) +
                    ", tại " + safe(branchName) + ". Chúng tôi gồm:",
                    font(bfR, 11, Font.ITALIC));
            dateLine.setAlignment(Element.ALIGN_LEFT);
            doc.add(dateLine);
            doc.add(Chunk.NEWLINE);

            // ── BÊN NHẬN ĐẶT CỌC (Bên A) ─────────────────────────────────────
            addUnderlineBold(doc, "Bên nhận đặt cọc", " (Sau đây gọi tắt là ", "Bên A", ")", bfR, bfB, 11);
            addFieldLine(doc, "Tên doanh nghiệp: ", "Công ty TNHH Kinh doanh Thương mại VinFast", bfR, bfB, 11);
            addFieldLine(doc, "Chi nhánh: ",         safe(branchName), bfR, bfB, 11);
            addFieldLine(doc, "Hotline: ",            hotline, bfR, bfB, 11);
            doc.add(Chunk.NEWLINE);

            // ── BÊN ĐẶT CỌC (Bên B) ──────────────────────────────────────────
            addUnderlineBold(doc, "Bên đặt cọc", " (Sau đây gọi tắt là ", "Bên B", ")", bfR, bfB, 11);
            addFieldLine(doc, "Họ và tên: ",    safe(customerName),  bfR, bfB, 11);
            addFieldLine(doc, "Email: ",         safe(customerEmail), bfR, bfB, 11);
            addFieldLine(doc, "Số điện thoại: ", safe(customerPhone), bfR, bfB, 11);
            doc.add(Chunk.NEWLINE);

            Paragraph intro = new Paragraph(
                    "Hai bên đồng ý thực hiện việc đặt cọc theo các thỏa thuận sau đây:",
                    font(bfR, 11, Font.ITALIC));
            doc.add(intro);
            doc.add(Chunk.NEWLINE);

            // ── ĐIỀU 1: TÀI SẢN ĐẶT CỌC ─────────────────────────────────────
            addArticleTitle(doc, "ĐIỀU 1:", "TÀI SẢN ĐẶT CỌC", bfB);
            addBodyText(doc,
                    "Bên B đặt cọc để đảm bảo việc mua chiếc xe ô tô dưới đây từ Bên A:",
                    bfR, 11);
            doc.add(Chunk.NEWLINE);

            PdfPTable carTable = new PdfPTable(2);
            carTable.setWidthPercentage(85);
            carTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            carTable.setWidths(new float[]{2f, 3f});
            addCarRow(carTable, "Nhãn hiệu",       "VinFast",        bfR, bfB);
            addCarRow(carTable, "Mẫu xe",            safe(carName),    bfR, bfB);
            addCarRow(carTable, "Chi nhánh bán",     safe(branchName), bfR, bfB);
            addCarRow(carTable, "Mã đặt cọc",        "#" + depositId,  bfR, bfB);
            doc.add(carTable);
            doc.add(Chunk.NEWLINE);

            // ── ĐIỀU 2: THỜI HẠN ĐẶT CỌC ────────────────────────────────────
            addArticleTitle(doc, "ĐIỀU 2:", "THỜI HẠN ĐẶT CỌC", bfB);
            addBodyText(doc,
                    "2.1. Thời hạn giữ xe: Bên A cam kết giữ xe cho Bên B trong vòng 30 ngày " +
                    "kể từ ngày " + DAY_FMT.format(now) + "/" + MONTH_FMT.format(now) + "/" + YEAR_FMT.format(now) +
                    " và không bán cho bên thứ ba trong thời gian này.",
                    bfR, 11);
            addBodyText(doc,
                    "2.2. Thời hạn hủy và hoàn tiền: Bên B được quyền hủy hợp đồng đặt cọc " +
                    "trong vòng 7 ngày làm việc kể từ ngày ký và được hoàn lại 100% số tiền cọc. " +
                    "Sau thời hạn trên, việc hủy đặt cọc sẽ theo chính sách quy định tại Điều 7.",
                    bfR, 11);
            doc.add(Chunk.NEWLINE);

            // ── ĐIỀU 3: MỤC ĐÍCH ĐẶT CỌC ────────────────────────────────────
            addArticleTitle(doc, "ĐIỀU 3:", "MỤC ĐÍCH ĐẶT CỌC", bfB);
            addBodyText(doc,
                    "Bên B đặt cọc nhằm đảm bảo việc giao kết và thực hiện Hợp đồng mua bán xe ô tô " +
                    "với Bên A. Số tiền đặt cọc sẽ được khấu trừ vào tổng giá trị hợp đồng mua bán " +
                    "khi hai bên ký kết hợp đồng chính thức.",
                    bfR, 11);
            doc.add(Chunk.NEWLINE);

            // ── ĐIỀU 4: SỐ TIỀN ĐẶT CỌC ─────────────────────────────────────
            addArticleTitle(doc, "ĐIỀU 4:", "SỐ TIỀN ĐẶT CỌC", bfB);

            PdfPTable payTable = new PdfPTable(2);
            payTable.setWidthPercentage(90);
            payTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            payTable.setWidths(new float[]{3f, 2f});
            addPaymentRow(payTable, "Số tiền đặt cọc", fmt(depositAmount), bfR, bfB, true);
            doc.add(payTable);
            doc.add(Chunk.NEWLINE);

            addBodyText(doc,
                    "Số tiền đặt cọc bằng chữ: " + amountToWords(depositAmount) + ".",
                    bfR, 11);
            doc.add(Chunk.NEWLINE);

            // ── ĐIỀU 5: QUYỀN VÀ NGHĨA VỤ CỦA BÊN A ────────────────────────
            addArticleTitle(doc, "ĐIỀU 5:", "QUYỀN VÀ NGHĨA VỤ CỦA BÊN A", bfB);

            addBoldSubItem(doc, "5.1. Bên A có các nghĩa vụ sau đây:", bfB, bfR, 11, new String[]{
                "a) Giữ xe cho Bên B trong thời hạn đặt cọc quy định tại Điều 2;",
                "b) Giao kết hoặc thực hiện nghĩa vụ dân sự đã thỏa thuận tại Điều 3 nêu trên. " +
                   "Nếu Bên A từ chối giao kết hoặc thực hiện nghĩa vụ dân sự (mục đích đặt cọc " +
                   "không đạt được) thì Bên A phải trả lại tiền cọc và một khoản tiền tương đương " +
                   "số tiền đặt cọc cho Bên B;",
                "c) Thông báo cho Bên B khi xe sẵn sàng để bàn giao."
            });
            doc.add(Chunk.NEWLINE);

            addBoldSubItem(doc, "5.2. Bên A có các quyền sau đây:", bfB, bfR, 11, new String[]{
                "a) Nhận lại tài sản đặt cọc từ Bên B hoặc được trừ khi thực hiện nghĩa vụ trả tiền " +
                   "cho Bên B trong trường hợp 2 bên giao kết hợp đồng mua bán (mục đích đặt cọc đạt được);",
                "b) Nhận lại và sở hữu tài sản đặt cọc trong trường hợp Bên B từ chối việc giao kết " +
                   "hoặc thực hiện nghĩa vụ dân sự (mục đích đặt cọc không đạt được)."
            });
            doc.add(Chunk.NEWLINE);

            // ── ĐIỀU 6: QUYỀN VÀ NGHĨA VỤ CỦA BÊN B ────────────────────────
            addArticleTitle(doc, "ĐIỀU 6:", "QUYỀN VÀ NGHĨA VỤ CỦA BÊN B", bfB);

            addBoldSubItem(doc, "6.1. Bên B có các nghĩa vụ sau đây:", bfB, bfR, 11, new String[]{
                "a) Nộp số tiền đặt cọc theo đúng thỏa thuận;",
                "b) Hoàn tất thủ tục mua xe và thanh toán phần còn lại trong thời hạn đặt cọc;",
                "c) Chịu mất tiền cọc nếu từ chối việc giao kết hoặc không đến nhận xe trong thời hạn."
            });
            doc.add(Chunk.NEWLINE);

            addBoldSubItem(doc, "6.2. Bên B có các quyền sau đây:", bfB, bfR, 11, new String[]{
                "a) Sở hữu tài sản đặt cọc nếu Bên A từ chối giao kết hoặc thực hiện nghĩa vụ dân sự;",
                "b) Được hoàn trả tiền cọc trong thời hạn hủy theo Điều 7 của hợp đồng này."
            });
            doc.add(Chunk.NEWLINE);

            // ── ĐIỀU 7: CHÍNH SÁCH HỦY VÀ HOÀN TIỀN ─────────────────────────
            addArticleTitle(doc, "ĐIỀU 7:", "CHÍNH SÁCH HỦY VÀ HOÀN TIỀN CỌC", bfB);

            // Khung nổi bật chính sách
            PdfPTable policyBox = new PdfPTable(1);
            policyBox.setWidthPercentage(100);
            PdfPCell policyCell = new PdfPCell();
            policyCell.setBackgroundColor(new Color(255, 248, 220));
            policyCell.setBorderColor(new Color(230, 180, 50));
            policyCell.setPadding(10);

            Paragraph p1 = new Paragraph("✓  ĐƯỢC HOÀN TIỀN CỌC 100%:", font(bfB, 11, Font.BOLD));
            p1.setSpacingAfter(4);
            policyCell.addElement(p1);
            policyCell.addElement(new Paragraph(
                    "Bên B được hoàn lại toàn bộ số tiền cọc nếu hủy hợp đồng đặt cọc này " +
                    "trong vòng 7 ngày làm việc kể từ ngày đặt cọc thành công. " +
                    "Yêu cầu hủy phải được thực hiện bằng văn bản hoặc qua hotline " + hotline + ".",
                    font(bfR, 11, Font.NORMAL)));

            Paragraph p2 = new Paragraph("\n✗  MẤT TIỀN CỌC:", font(bfB, 11, Font.BOLD));
            p2.setSpacingAfter(4);
            policyCell.addElement(p2);
            policyCell.addElement(new Paragraph(
                    "Bên B sẽ mất toàn bộ số tiền cọc trong các trường hợp sau:\n" +
                    "  • Hủy hợp đồng sau 7 ngày làm việc kể từ ngày đặt cọc;\n" +
                    "  • Không đến nhận xe trong thời hạn đặt cọc (30 ngày) mà không có lý do chính đáng;\n" +
                    "  • Từ chối ký kết hợp đồng mua bán chính thức sau khi xe đã sẵn sàng.",
                    font(bfR, 11, Font.NORMAL)));

            policyBox.addCell(policyCell);
            doc.add(policyBox);
            doc.add(Chunk.NEWLINE);

            addBodyText(doc,
                    "Thời gian hoàn tiền: Bên A sẽ hoàn trả tiền cọc trong vòng 3-5 ngày làm việc " +
                    "kể từ khi nhận được yêu cầu hủy hợp lệ.",
                    bfR, 11);
            doc.add(Chunk.NEWLINE);

            // ── ĐIỀU 8: PHƯƠNG THỨC GIẢI QUYẾT TRANH CHẤP ───────────────────
            addArticleTitle(doc, "ĐIỀU 8:", "PHƯƠNG THỨC GIẢI QUYẾT TRANH CHẤP", bfB);
            addBodyText(doc,
                    "Trong quá trình thực hiện Hợp đồng mà phát sinh tranh chấp, các bên cùng nhau " +
                    "thương lượng, giải quyết trên nguyên tắc tôn trọng quyền lợi của nhau; trong " +
                    "trường hợp không giải quyết được, thì một trong hai bên có quyền khởi kiện để " +
                    "yêu cầu Tòa án có thẩm quyền giải quyết theo quy định của pháp luật.",
                    bfR, 11);
            doc.add(Chunk.NEWLINE);

            // ── ĐIỀU 9: CAM ĐOAN CỦA CÁC BÊN ────────────────────────────────
            addArticleTitle(doc, "ĐIỀU 9:", "CAM ĐOAN CỦA CÁC BÊN", bfB);
            addBodyText(doc,
                    "Bên A và Bên B chịu trách nhiệm trước pháp luật về những lời cam đoan sau đây:",
                    bfR, 11);
            addNumberedItems(doc, bfR, 11, new String[]{
                "Việc giao kết Hợp đồng này hoàn toàn tự nguyện, không bị lừa dối hoặc ép buộc;",
                "Thực hiện đúng và đầy đủ tất cả các thỏa thuận đã ghi trong Hợp đồng này;",
                "Các thông tin cung cấp trong hợp đồng là đúng sự thật và chịu trách nhiệm trước pháp luật."
            });
            doc.add(Chunk.NEWLINE);

            // ── ĐIỀU 10: ĐIỀU KHOẢN CHUNG ────────────────────────────────────
            addArticleTitle(doc, "ĐIỀU 10:", "ĐIỀU KHOẢN CHUNG", bfB);
            addNumberedItems(doc, bfR, 11, new String[]{
                "Hai bên hiểu rõ quyền, nghĩa vụ và lợi ích hợp pháp của mình được thỏa thuận trong hợp đồng này.",
                "Hai bên đã tự đọc lại hợp đồng này, đã hiểu và đồng ý tất cả các điều khoản đã ghi trong hợp đồng.",
                "Hợp đồng này có hiệu lực kể từ ngày các bên ký vào Hợp đồng này.",
                "Hợp đồng được lập thành 02 (hai) bản, mỗi bên giữ một bản và có giá trị như nhau."
            });
            doc.add(Chunk.NEWLINE);

            Paragraph italic = new Paragraph(
                    "Hợp đồng được lập thành 02 (hai) bản, mỗi bên giữ một bản và có giá trị như nhau.",
                    font(bfR, 11, Font.ITALIC));
            italic.setAlignment(Element.ALIGN_CENTER);
            doc.add(italic);
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);

            // ── CHỮ KÝ ───────────────────────────────────────────────────────
            addDepositSignatureBlock(doc, customerName, branchName, now, bfR, bfB);

            doc.close();
            log.info("[PDF] generateDepositContract depositId={} size={}B", depositId, baos.size());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("[PDF] generateDepositContract failed depositId={} err={}", depositId, e.getMessage(), e);
            throw new RuntimeException("Tạo PDF hợp đồng đặt cọc thất bại: " + e.getMessage(), e);
        }
    }

    private void addBoldSubItem(Document doc, String header, BaseFont bfB, BaseFont bfR,
                                 float size, String[] items) throws DocumentException {
        Paragraph h = new Paragraph(header, font(bfB, size, Font.BOLD));
        h.setSpacingBefore(4);
        doc.add(h);
        for (String item : items) {
            Paragraph p = new Paragraph(item, font(bfR, size, Font.NORMAL));
            p.setIndentationLeft(20);
            p.setSpacingBefore(3);
            doc.add(p);
        }
    }

    private void addDepositSignatureBlock(Document doc, String customerName,
                                           String branchName, LocalDateTime now,
                                           BaseFont bfR, BaseFont bfB) throws DocumentException {
        Paragraph dateLine = new Paragraph(
                "Làm tại ............., ngày " + DAY_FMT.format(now) +
                " tháng " + MONTH_FMT.format(now) + " năm " + YEAR_FMT.format(now),
                font(bfR, 11, Font.ITALIC));
        dateLine.setAlignment(Element.ALIGN_RIGHT);
        doc.add(dateLine);
        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);

        PdfPTable sig = new PdfPTable(2);
        sig.setWidthPercentage(100);

        PdfPCell a = new PdfPCell();
        a.setBorder(Rectangle.NO_BORDER);
        a.addElement(center("BÊN A", bfB, 12));
        a.addElement(center("(Bên nhận đặt cọc)", bfR, 10));
        a.addElement(center("(Ký, điểm chỉ và ghi rõ họ tên)", bfR, 10));
        a.addElement(new Phrase("\n\n\n\n\n"));
        a.addElement(center(safe(branchName), bfB, 11));
        sig.addCell(a);

        PdfPCell b = new PdfPCell();
        b.setBorder(Rectangle.NO_BORDER);
        b.addElement(center("BÊN B", bfB, 12));
        b.addElement(center("(Bên đặt cọc)", bfR, 10));
        b.addElement(center("(Ký, điểm chỉ và ghi rõ họ tên)", bfR, 10));
        b.addElement(new Phrase("\n\n\n\n\n"));
        b.addElement(center(safe(customerName), bfB, 11));
        sig.addCell(b);

        doc.add(sig);
    }
}