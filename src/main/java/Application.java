import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.IntelliJTheme;
import gui.MainFrame;
import gui.GlobalUtils;

import javax.swing.*;
import java.awt.*;

public class Application {
    public static void main(String[] args) {
        GlobalUtils.initGlobalDefaultFont(new Font("微软雅黑", Font.PLAIN, 12));
        SwingUtilities.invokeLater(() -> {
            LafManager.setTheme(new IntelliJTheme());
            LafManager.install();
            String productFilePath = args.length > 0 ? args[0] : "";
            new MainFrame("商城系统DEMO", 1000, 800, productFilePath).display();
        });
    }
}
