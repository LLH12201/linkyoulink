package org.llh.weixin.message.req;

/**
 * �ı���Ϣ
 * 
 * @author llh
 * @date 2014-09-11
 */
public class TextMessage extends BaseMessage {
	// ��Ϣ����
	private String Content;

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}
}
