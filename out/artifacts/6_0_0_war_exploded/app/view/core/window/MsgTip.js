/**
 * 右下角消息提示类型1
 */
Ext.define('erp.view.core.window.MsgTip', {
	extend: 'Ext.window.Window',
	alias: 'widget.msgtip',
	frame: true,
	closable: false,
	bodyStyle: 'background: #E0EEEE',
	width: 400,
	height:120,
	renderTo: Ext.getBody(),
	x: Ext.getBody().getWidth()-400, 
	y: Ext.getBody().getHeight()-120,
	autoClose: true,
	closeAction: 'destroy',
	autoCloseTime: 8000,
	tools: [{
		type: 'close',
		handler: function(btn){
			var me = arguments[2].ownerCt;
			me.fireEvent('close', me);
			me.close();
		}
	}],
	initComponent: function() {
		this.id = 'msg-win-' + this.prId;
		this.addEvents({
			'close': true,
			'check': true,
			'reply': true
		});
		this.title = '<div style="height:25;padding-top:5px;color:#FF6A6A;background: #E0EEEE url(' + basePath + 
			'resource/ext/resources/themes/images/default/grid/grid-blue-hd.gif) repeat center center">&nbsp;' + this.title + '</div>';
		this.callParent(arguments);
		this.setPosition(Ext.getBody().getWidth()-400,Ext.getBody().getHeight()-120);
		this.show();
		this.el.slideIn('r', { duration: 1000 });
		this.insertMsg(this.from, this.date, this.url, this.transImages(this.context));
	},
	insertMsg: function(from, date, url, context){
		this.down('displayfield[name=from]').getEl().dom.childNodes[0].innerHTML = '<font color=blue style="font-family:宋体;font-size:12px;padding-left:3px;">' + from + ':</font>' + 
			"<font color=green style='margin-left:5px;'>(" + date + ")</font>";
		//取消流程提醒知会能点中其他信息
		if(context!=null && context.indexOf('a href')>-1){
			this.down('displayfield[name=context]').setValue(context);
		}else this.down('displayfield[name=context]').setValue("<a href='javascript:Ext.getCmp(\"" + this.id + "\").close();openUrl(\"" + url + "\");'>" + context + "</a>");
	},
	items: [{
		xtype: 'displayfield',
		height: 15,
		width: 400,
		fieldLabel: '',
		labelWidth: 200,
		labelSeparator: '',
		fieldStyle: 'color:green',
		name: 'from'
	},{
		xtype: 'displayfield',
		labelSeparator: '',
		height: 40,
		width: 400,
		fieldStyle: 'padding-left:30px;',
		name: 'context'
	},{
		xtype: 'button',
		width: 60,
		style: {
			marginLeft: '60px',
			marginTop:'15px',
		},
		text: '查&nbsp;看',
		cls: 'x-btn-blue',
		handler: function(btn){
			var me = btn.ownerCt;
			me.fireEvent('check', me);
			me.close();
		}
	},{
		xtype: 'button',
		width: 60,
		style: {
			marginLeft: '3px',
			marginTop:'15px',
		},
		text: '回&nbsp;复',
		cls: 'x-btn-blue',
		handler: function(btn){
			var me = btn.ownerCt;
			me.fireEvent('reply', me);
			me.close();
		}
	},{
		xtype: 'button',
		width: 60,
		style: {
			marginLeft: '3px',
			marginTop:'15px',
		},
		text: '更&nbsp;多',
		cls: 'x-btn-blue',
		handler: function(btn){
			var me = btn.ownerCt;
			openUrl2('jsps/oa/info/pagingReceive.jsp?whoami=PagingRelease&urlcondition=prd_recipientid=' + em_uu + ' AND prd_status=-1', '未阅寻呼');
			me.fireEvent('close', me);
			me.close();
		}
	},{
		xtype: 'button',
		width: 60,
		style: {
			marginLeft: '3px',
			marginTop:'15px',
		},
		text: '关&nbsp;闭',
		cls: 'x-btn-blue',
		handler: function(btn){
			var me = btn.ownerCt;
			me.fireEvent('close', me);
			me.close();
		}
	},{
		xtype: 'button',
		width: 80,
		style: {
			marginLeft: '3px',
			marginTop:'15px',
		},
		text: '全部关闭',
		cls: 'x-btn-blue',
		handler: function(btn){
			var win = parent.Ext.ComponentQuery.query('msgtip');
			if(win){
				Ext.each(win, function(){
					this.close();
				});
		  }
		}		
	}],
	transImages: function(msg){
		msg = msg.toString();
		var faces = msg.match(/&f\d+;/g);
		Ext.each(faces, function(f){//表情
			msg = msg.replace(f, '<img src="' + basePath + 'resource/images/face/' + f.substr(2).replace(';', '') + '.gif">');
		});
		var images = msg.match(/&img\d+;/g);
		Ext.each(images, function(m){//图片
			msg = msg.replace(m, '');
		});
		return msg;
	}
});