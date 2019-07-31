
public class PostRequestHandler {
  
  public void onRequest(QueryStringParser params) {
    if(params.get("canHaveCandy").equals("true")) {
      // can get candy
      myPort.write('1');
      //myPort.write("N");
    } else {
      // cannot get candy
    }
  }
}