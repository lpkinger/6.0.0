// EventSource that supports post data
Ext.define('erp.util.EventSource', {
	onMessage: Ext.emptyFn,
	onProgress: Ext.emptyFn,
	onComplete: Ext.emptyFn,
	onError: function(e){
		showError(e);
	},
	showProgress: true,
	constructor: function (options) {
        Ext.apply(this, options);
        this.init();
    },
    init: function() {
    	var formData = null, me = this, data = me.data;
        if (data) {
            formData = new FormData();
            for (var k in data) {
                formData.append(k, data[k]);
            }
        }
        var xhr = new XMLHttpRequest();
        xhr.open((formData ? 'POST' : 'GET'), me.url, true);
        xhr.onreadystatechange = function () {
            if (xhr.status != 200) {
            	if (xhr.readyState == 4 && xhr.responseText) {
	            	me.updateText('出现错误');
	            	me.updateProgress(1);
	            	me.onError(xhr.responseText);
	            	this.showProgress = false;
            	}
            } else {
                var state = xhr.readyState;
                var resText = {};
                if (xhr.readyState > 2) {
                	if (xhr.readyState == 4 && !xhr.responseText) {
                		me.updateText('执行完成');
                    	me.updateProgress(1);
                    	me.onComplete({});
                	} else {
                		if(xhr.responseText.indexOf("exceptionInfo")>=0){
                			resText.error = xhr.responseText;
                			me.parseResponseText(JSON.stringify(resText));	
                		}else{
                			me.parseResponseText(xhr.responseText);	
                		}
                	}
                }
            }
        }
        xhr.setRequestHeader('Accept', 'text/event-stream, */*');
        xhr.send(formData);
        me.updateProgress(0);
    },
    parseResponseText: function(text) {
    	var me = this, messages = text.split(/^data:|\ndata:/g).filter(function (msg) {
            return !!msg;
        });
        var o = me.receivedSize || 0, n = messages.length, msg;
        for (; o < n; o++) {
            try {
                msg = JSON.parse(messages[o]);
            } catch (e) {
                continue;
            }
            if (msg.error) {
            	me.destroyMessageBox();
            	me.onError(msg.error);
            } else if (msg.result) {
            	me.updateText('执行完成');
            	me.updateProgress(1);
            	me.onComplete(msg.result);
            } else if (msg.progress) {
            	if (msg.progress > 0 && msg.progress < 1) {
            		me.updateProgress(msg.progress);
            	}
            	me.onProgress(msg.progress);
            } else if (msg.success) {
            	me.updateText('处理成功');
            	me.updateProgress(1);
            	me.onComplete(msg);
            } else {
            	me.updateText(msg.info || msg.warning);
            	// info、warning
            	me.onMessage(msg);
            }
        }
        me.receivedSize = n;
    },
    getMessageBox: function() {
    	var me = this, box = me.messageBox;
    	if (!box) {
    		box = me.messageBox = Ext.Msg.show(Ext.apply({
    			modal: true,
    			msg: '请稍等...'
    		}, me.showProgress ? {
    			progress: true
    		} : {
    			title: '提示',
    			icon: Ext.MessageBox.INFO
    		}));
    	}
    	return box;
    },
    updateProgress: function(value) {
    	var box = this.getMessageBox();
    	if (this.showProgress) {
    		box.updateProgress(value, Math.floor(value * 100) + '%');
    	}
    	if (value >= 1||!this.showProgress) {
			this.destroyMessageBox();
		}
    },
    updateText: function(text) {
    	this.getMessageBox().updateText(text);
    },
    destroyMessageBox: function() {
    	if (this.messageBox) {
    		this.getMessageBox().close();
    		this.messageBox = null;
    	}
    }
});