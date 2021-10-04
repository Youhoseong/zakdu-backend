package capstone.jakdu.encrypt;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class EncryptTest {
    @Test
    public void printAllContents() throws IOException {
        String fileName = "수학의힘알파중1-1.pdf";
        int page = 10;

        File source = new File(fileName);
        PDFont font = PDType1Font.HELVETICA_BOLD;
        PDDocument pdfDoc = PDDocument.load(source);
        PDPage pdfDocPage = pdfDoc.getPage(page);
        PDFStreamParser parser = new PDFStreamParser(pdfDocPage);
        parser.parse();
        List<Object> tokens = parser.getTokens();
        /*tokens.forEach(token -> {

            if (token instanceof  Operator) {
                String pstring = "";
                int prej = 0;
                Operator op = (Operator) token;
                System.out.println("name: " + op.getName());
                //Tj and TJ are the two operators that display strings in a PDF
                if (op.getName().equals("Tj"))
                {
                    // Tj takes one operator and that is the string to display so lets update that operator
                    COSString previous = (COSString) token;
                    String string = previous.getString();
                    //string = string.replaceFirst(searchString, replacement);
                    previous.setValue(string.getBytes());
                } else if (op.getName().equals("TJ"))
                {
                    COSArray previous = (COSArray) token;
                    for (int k = 0; k < previous.size(); k++)
                    {
                        Object arrElement = previous.getObject(k);
                        if (arrElement instanceof COSString)
                        {
                            COSString cosString = (COSString) arrElement;
                            String string = cosString.getString();

                            if (j == prej) {
                                pstring += string;
                            } else {
                                prej = j;
                                pstring = string;
                            }
                        }
                    }
                }
            }



                if(op.getImageData() != null) {
                    System.out.println("Image!");
                }
            }


        });*/
        PDPageContentStream contentStream = new PDPageContentStream(pdfDoc, pdfDocPage, PDPageContentStream.AppendMode.APPEND, true);
        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.newLineAtOffset(150, 500);
        contentStream.showText("Hello World");
        contentStream.endText();
        contentStream.close();
        pdfDoc.setAllSecurityToBeRemoved(true);
        pdfDoc.save("test.pdf");
        pdfDoc.close();
    }
}
