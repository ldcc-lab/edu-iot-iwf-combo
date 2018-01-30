package kr.co.ldcc.edu.iot;

import java.io.IOException;
import java.util.Map;

import comus.wp.onem2m.iwf.nch.NotifyResponse;
import comus.wp.onem2m.iwf.run.CmdListener;
import comus.wp.onem2m.iwf.run.IWF;

public class ComboDevice {
  //
  Runtime rt = Runtime.getRuntime();
  Process p = null;

  private IWF vDevice;
  private String temperature;
  private String OID = "<OID>"; // 디바이스 식별체계
  private String req;

  private void register() {
    //
    try {
      vDevice = new IWF(OID); // 1. 환경설정 (OID, conf, log)
    } catch (Exception e) {
      throw new ComboDeviceException(e.getMessage());
    }
    vDevice.register(); // 2. IWF 등록
  }

  private void listen() {
    if (vDevice != null) {
      //
      try {
        rt.exec("gpio mode 0 out");
      } catch (IOException e) {
        throw new ComboDeviceException(e.getMessage());
      }
      CmdListener myDeviceListener = new CmdListener() {

        @Override
        public void excute(Map<String, String> cmd, NotifyResponse resp) {
          if ((req = cmd.get("switch")) != null) {
            if ("1".equals(req)) {
              try {
                rt.exec("gpio write 0 1");
              } catch (IOException e) {
                System.out.println(e.getMessage());
              }
            } else if ("0".equals(req)) {
              try {
                rt.exec("gpio write 0 0");
              } catch (IOException e) {
                System.out.println(e.getMessage());
              }
            }
            vDevice.putContent("control-switch", "text/plain", "" + req);
          } else {
            //
          }

        }
      };
      vDevice.addCmdListener(myDeviceListener);
    } else {
      throw new ComboDeviceException("등록을 먼저 진행해 주세요.");
    }
  }

  private void check() throws InterruptedException {
    //
    if(vDevice != null) {
       while(true) {
         vDevice.putContent("tmperature", "text/plain", "" + 30);
         
         Thread.sleep(10000);
       }
    } else {
      throw new ComboDeviceException("등록을 먼저 진행해주세요.");
    }
  }

  public static void main(String[] args) throws InterruptedException {
    //
    ComboDevice myDevice = new ComboDevice();

    myDevice.register();
    myDevice.listen();
    myDevice.check();
  }

}
