package capstone.jakdu.refactoring;

public class Regex {
    public static boolean isAllTextIsPrefix(String text, int prefixId) {
        text = text.replaceAll("\\s+", "");

        switch (prefixId) {
            case 0:
                return text.matches("^[0-9]{2}$");
            case 1:
                return text.matches("^(I|II|III|IV|V|VI|VII|VIII|IX|X|XI|XII|Ⅳ|Ⅲ)$");
            case 2:
                return text.matches("^[1-9]$");
            case 3:
                return text.matches("^[A-Z]$");
            case 4:
                return text.matches("^(가|나|다|라|마|바|사|아|자|차|카|타|파|하)$");
            case 5:
                return text.matches("^(유형)\\d{2}$");
            case 6:
                return text.matches("^(•|●)$");
            case 7:
                return text.matches("^(첫|둘|셋|넷|다섯|여섯|일곱|여덟|아홉|열)째$");
        }
        return false;
    }

    public static int getPrefixNum(String text) {
        /** 모든 공백 1칸, 숫자 뒤에 점 또는 공백이 오는 거로 수정필요 **/
        text = text.replaceAll("\\s+", " ");

        if(text.matches("^[0-9]{2}((\\s|\\.).*)?$"))
            return 0;
        else if(text.matches("^(I|II|III|IV|V|VI|VII|VIII|IX|X|XI|XII|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|Ⅵ|Ⅶ|Ⅷ|Ⅸ|Ⅹ|Ⅺ|Ⅻ)((\\s|\\.).*)?$"))
            return 1;
        else if(text.matches("^(Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|Ⅵ|Ⅶ|Ⅷ|Ⅸ|Ⅹ|Ⅺ|Ⅻ).*$")) // 뒤에 공백 없어도됨
            return 1;
        else if(text.matches("^[1-9]((\\s|\\.).*)?$"))
            return 2;
        else if(text.matches("^[A-Z]((\\s|\\.).*)?$"))
            return 3;
        else if(text.matches("^(가|나|다|라|마|바|사|아|자|차|카|타|파|하)((\\s|\\.).*)?$"))
            return 4;
        else if(text.matches("^(유형)\\s?([0-9]{1,2})((\\s|\\.).*)?$"))
            return 5;
        else if(text.matches("^(•|●|■)((\\s|\\.).*)?$"))
            return 6;
        else if(text.matches("^(첫|둘|셋|넷|다섯|여섯|일곱|여덟|아홉|열)\\s?째((\\s|\\.).*)?$"))
            return 7;
        else if(text.matches("^.*(?i)chapter.*$"))
            return 8;
        else if(text.matches("^(?i)(section).*$"))
            return 9;
        else  // 접두어가 없을 때
            return 10;

    }

    public static boolean isStartPrefix(String text, int prefixId) {
        text = text.replaceAll("\\s+", " ");

        switch (prefixId) {
            case 0:
                return text.matches("^01((\\s|\\.).*)?$");
            case 1:
                return text.matches("^I((\\s|\\.).*)?$");
            case 2:
                return text.matches("^1((\\s|\\.).*)?$");
            case 3:
                return text.matches("^A((\\s|\\.).*)?$");
            case 4:
                return text.matches("^(가)((\\s|\\.).*)?$");
            case 5:
                return text.matches("^(유형)\\s?(1|01)((\\s|\\.).*)?$");
            case 6:
                return text.matches("^(•|●)((\\s|\\.).*)?$");
            case 7:
                return text.matches("^첫째((\\s|\\.).*)?$");
        }
        return true;

    }

    public static boolean isDate(String input) {

        String regex = ".*[0-9]{2,4}.[0-9]{1,2}.[0-9]{1,2}.*";
        if(input.matches(regex)) {
            return true;
        }else {
            return false;
        }

    }

    public static boolean hasPageNum(String s) {
        String s1 = s.replaceAll("(\\s| )+", " ");
        System.out.println("s1 = " + s1);
        return s1.matches("(.*\\s\\d+|\\d+)");
    }
}
