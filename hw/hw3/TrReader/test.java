import java.io.IOException;
import java.io.StringReader;

public class test {
	public static void main(String[] args) throws IOException {
		StringReader S = new StringReader("aaaaa");
		char[] buffer = new char[5];
		System.out.println(S.read());
		System.out.println(S.read(buffer));
		System.out.println(S.read(buffer, 0, 5));
	}
}
