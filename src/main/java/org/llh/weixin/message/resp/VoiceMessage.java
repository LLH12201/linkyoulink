package org.llh.weixin.message.resp;

/**
 * ������Ϣ
 * 
 * @author llh
 * @date 2014-09-11
 */
public class VoiceMessage extends BaseMessage {
	// ����
	private Voice Voice;

	public Voice getVoice() {
		return Voice;
	}

	public void setVoice(Voice voice) {
		Voice = voice;
	}
}
