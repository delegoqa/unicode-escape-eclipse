package uescape.view;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class UnicodeEscapeView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "uescape.view.UnicodeEscapeView";

	/** the component that keeps normal text */
	private Text unicode;

	/** the component that keeps escaped text */
	private Text escaped;

	public UnicodeEscapeView() {}

	/**
	 *Generate View
	 */
	public void createPartControl(Composite parent) {
		Composite grid = new Composite(parent, 0);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			grid.setLayout(layout);
			GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
			gd.widthHint = 400;
			grid.setLayoutData(gd);
		}
		{
			Label label = new Label(grid, 0);
			GridData gd = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
			label.setLayoutData(gd);
			label.setText("Unicode:");
		}
		{
			Label label = new Label(grid, 0);
			GridData gd = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
			label.setLayoutData(gd);
			label.setText("Escaped:");
		}
		{
			Text text = new Text(grid, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
			GridData gd = new GridData(GridData.FILL_BOTH);
			text.setLayoutData(gd);
			text.addKeyListener(new KeyListener() {
				public void keyReleased(KeyEvent e) {
					escapeUnicode();
				}
				public void keyPressed(KeyEvent e) {}
			});
			unicode = text;
		}
		{
			Text text = new Text(grid, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
			GridData gd = new GridData(GridData.FILL_BOTH);
			text.setLayoutData(gd);
			text.addKeyListener(new KeyListener() {
				public void keyReleased(KeyEvent e) {
					unescapeUnicode();
				}
				public void keyPressed(KeyEvent e) {}
			});
			escaped = text;
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		unicode.setFocus();
	}

	private void escapeUnicode() {
		final String sourceStr = unicode.getText();
		final StringBuilder escapedString = new StringBuilder();

		if (sourceStr != null && sourceStr.length() > 0) {
			final int len = sourceStr.length();
			for (int i = 0; i < len; i++) {
				final char c = sourceStr.charAt(i);
				if (c > 0x7f) {
					escapedString.append('\\');
					escapedString.append('u');
					String hex = Integer.toHexString((int) c);
					for (int j = hex.length(); j < 4; j++) {
						escapedString.append('0');
					}
					escapedString.append(hex);
				} else {
					escapedString.append(c);
				}
			}
		}
		escaped.setText(escapedString.toString());
	}

	private void unescapeUnicode() {
		final String sourceStr = escaped.getText();
		final StringBuilder unescapedString = new StringBuilder();

		if (sourceStr != null && sourceStr.length() > 0) {
			final int len = sourceStr.length();

			for (int i = 0; i < len;) {
				char c = sourceStr.charAt(i++);
				if (c == '\\' && i < len) {
					c = sourceStr.charAt(i++);
					if (c == 'u' && i < len) {
						int charCode = 0;
						for (int j = 0; j < 4 && i < len; j++) {
							c = sourceStr.charAt(i++);
							switch (c) {
								case 48: // '0'
								case 49: // '1'
								case 50: // '2'
								case 51: // '3'
								case 52: // '4'
								case 53: // '5'
								case 54: // '6'
								case 55: // '7'
								case 56: // '8'
								case 57: // '9'
									charCode = ((charCode << 4) + c) - 48;
									break;

								case 97: // 'a'
								case 98: // 'b'
								case 99: // 'c'
								case 100: // 'd'
								case 101: // 'e'
								case 102: // 'f'
									charCode = ((charCode << 4) + 10 + c) - 97;
									break;

								case 65: // 'A'
								case 66: // 'B'
								case 67: // 'C'
								case 68: // 'D'
								case 69: // 'E'
								case 70: // 'F'
									charCode = ((charCode << 4) + 10 + c) - 65;
									break;

								case 58: // ':'
								case 59: // ';'
								case 60: // '<'
								case 61: // '='
								case 62: // '>'
								case 63: // '?'
								case 64: // '@'
								case 71: // 'G'
								case 72: // 'H'
								case 73: // 'I'
								case 74: // 'J'
								case 75: // 'K'
								case 76: // 'L'
								case 77: // 'M'
								case 78: // 'N'
								case 79: // 'O'
								case 80: // 'P'
								case 81: // 'Q'
								case 82: // 'R'
								case 83: // 'S'
								case 84: // 'T'
								case 85: // 'U'
								case 86: // 'V'
								case 87: // 'W'
								case 88: // 'X'
								case 89: // 'Y'
								case 90: // 'Z'
								case 91: // '['
								case 92: // '\\'
								case 93: // ']'
								case 94: // '^'
								case 95: // '_'
								case 96: // '`'
								default:
									unescapedString.append("\n!!! Malformed \\uxxxx encoding. !!!\n");
							}
						}

						unescapedString.append((char) charCode);
					} else {
						if (c == 't') c = '\t';
						else if (c == 'r') c = '\r';
						else if (c == 'n') c = '\n';
						else if (c == 'f') c = '\f';
						unescapedString.append(c);
					}
				} else {
					unescapedString.append(c);
				}
			}
		}
		unicode.setText(unescapedString.toString());
	}
}