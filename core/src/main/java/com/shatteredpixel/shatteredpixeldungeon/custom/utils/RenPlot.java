package com.shatteredpixel.shatteredpixeldungeon.custom.utils;

import com.shatteredpixel.shatteredpixeldungeon.windows.WndDialog;

public class RenPlot extends Plot {

    public static final String PLOT_NAME = "sewer";

    private final static int maxprocess = 3;

    {
        process = 1 ;
    }

    protected String getPlotName() {
        return SEWER_NAME;
    }

    @Override
    public void reachProcess(WndDialog wndDialog) {
        diagulewindow = wndDialog;

        while(this.process < needed_process )
        {
            this.process();
        }
    }

    @Override
    public void process() {
        if(diagulewindow!=null) {
            switch (process) {
                default:
                case 1:
                    process_to_1();//Mostly process to 1 is made directly when creating,it might not be used,just in case
                    break;
                case 2:
                    process_to_2();
                    break;
                case 3:
                    process_to_3();
                    break;
//                case 4:
//                    process_to_4();
//                    break;
//                case 5:
//                    process_to_5();
//                    break;
//                case 6:
//                    process_to_6();
//                    break;
//                case 7:
//                    process_to_7();
//                    break;
//                case 8:
//                    process_to_8();
//                    break;
//                case 9:
//                    process_to_9();
//                    break;
//                case 10:
//                    process_to_10();
//                    break;
            }
            diagulewindow.update();
            process ++;
        }
    }

    @Override
    public void initial(WndDialog wndDialog) {
        diagulewindow = wndDialog;
        process = 2;
        process_to_1();
    }

    @Override
    public boolean end() {
        return process > maxprocess;
    }

    @Override
    public void skip() {
        diagulewindow.cancel();
        WndDialog.settedPlot = null;
    }

    private void process_to_1()
    {
        diagulewindow.hideAll();
        diagulewindow.showBackground("REN:一直以来，我知道更多的灾难正在发生。你是否也是灾难中失去挚友的那一位？");
    }

    private void process_to_2()
    {
        diagulewindow.hideAll();
        diagulewindow.showBackground("REN:你好，我是REN-33!我是唯一的个体……,有一些时候，你是否在想这个世界发生了什么吗？其实，你应该试着放松，那就是你最好的方式！");
    }

    private void process_to_3()
    {
        diagulewindow.hideAll();
        diagulewindow.showBackground("REN:枪击毙了穹顶天上的椋鸟群落，此时即可眺望繁星、即可观测天津星肆、即可瞭望海之灯塔，为之祈祷。");
    }

//    private void process_to_4()
//    {
//        diagulewindow.setMainAvatar(Script.Portrait(Script.Character.CHEN));
//        diagulewindow.setLeftName(Script.Name(Script.Character.CHEN));
//        diagulewindow.changeText(Messages.get(this, "txt1"));
//    }

//    private void process_to_5()
//    {
//        diagulewindow.darkenMainAvatar();
//
//        diagulewindow.setSecondAvatar(Script.Portrait(Script.Character.RED));
//        diagulewindow.setRightName(Script.Name(Script.Character.RED));
//        diagulewindow.changeText(Messages.get(this, "txt2"));
//    }

}
