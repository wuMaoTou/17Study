import com.maotou.bsdiff.BsDiffUtil;

public class BsDiffTest {

    public static void main(String[] args) {
        String root = "./src/";
        //如果用idea编译运行要添加src路径,用终端在src目录下则不用
        if (args.length > 0 && "shell".equals(args[0])){
            root = "";
        }
        String oldFile = root + "old.apk";
        String newFile = root + "new.apk";
        String patchFile = root + "old-to-new.patch";

        int result = BsDiffUtil.getInstance().bsDiffFile(oldFile, newFile, patchFile);
        System.out.println(result);
    }
}
