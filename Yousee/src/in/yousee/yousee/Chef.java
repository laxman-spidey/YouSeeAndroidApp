package in.yousee.yousee;

import in.yousee.yousee.model.CustomException;

public interface Chef
{
	public void assembleRequest();
	public void cook() throws CustomException;
	public void serveResponse(String result);

}
