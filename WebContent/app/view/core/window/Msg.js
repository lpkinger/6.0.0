/**
 * 右下角消息提示类型2
 */
Ext.define('erp.view.core.window.Msg', {
	extend: 'Ext.window.Window',
	alias: 'widget.msg',
	cls:'x-win-msg',
	frame: true,
	closable: false,
	bodyStyle: 'background: #E0EEEE;',
	width: 400,
	height: 320,
	clientY: 465,
	renderTo: Ext.getBody(),
	x: screen.width - 400 -10, 
	y: screen.height - 465 - 10,
	tools: [{
		cls:'x-msg-tools',
		type: 'close',
		handler: function(btn){
			var me = arguments[2].ownerCt;
			me.fireEvent('close', me);
			me.destroy();
		}
	}],
	autoClose: true,//自动关闭
	closeAction: 'destroy',
	autoCloseTime: 8000,
	isError: true,//是否为出错提示
	initComponent: function() {
		this.autoCloseTime = this.autoCloseTime || (this.isDebug() ? 60000 : 8000);
		this.addEvents({
			'close': true
		});
		if(!this.isError){
			this.buttons = null;
			this.clientY = 430;
		}
		if (this.autoCloseTime <= 3000) {// fast slide in and out
			this.width = 300;
			this.height = 150;
			this.x = screen.width - 300 -10;
			this.y = screen.height - 150 - 10;
			this.clientY = 300;
			this.context = '<font size=4 color=blue>' + this.context + '</font>';
		}
		this.title = '<div class = "x-msg-head">提醒</div>';
		this.callParent(arguments);
		this.updatePosition();
		this.insertMsg(this.context);
		var me = this;
		if(me.autoClose){//自动关闭
			setTimeout(function(){
				me.destroy();
			}, me.autoCloseTime);
		}
	},
	bbar: ['->',{
		width: 100,
		text: '导出错误信息',
		cls: 'x-btn-errormsg',
		handler: function(btn){
			var me = btn.ownerCt.ownerCt;
			me.sendError(me);
			me.close();
		},
		listeners:{
			afterrender:function(btn){	
				var me = btn.ownerCt.ownerCt;	
				if(!me.isDebug()){
					btn.hide();
				}
			}
		}
	}/*,{
		width: 60,
		style: {
			marginLeft: '3px'
		},
		text: '帮助',
		cls: 'x-btn-blue',
		handler: function(btn){
			var me = btn.ownerCt.ownerCt;
			me.help();
			me.close();
		}
	},{
		width: 60,
		style: {
			marginLeft: '3px'
		},
		text: '关&nbsp;闭',
		cls: 'x-btn-blue',
		handler: function(btn){
			var me = btn.ownerCt.ownerCt;
			me.fireEvent('close', me);
			me.destroy();
		}
	}*/],
	updatePosition: function(){
		var count = Ext.ComponentQuery.query('msg').length;
		this.setPosition(screen.width - this.width - count*30 - 10, screen.height - this.clientY - 10);
		this.show();
		this.el.slideIn('r', { duration: 500 });
	},
	insertMsg: function(msg){
		this.insert(0, {
			xtype: 'panel',
			height: '100%',
			autoScroll: true,
			html: '<div style="font-size:14px;padding: 5px 10px">' + msg + '</div>'
		});
	},
	sendError: function(msg){
		var all = msg.context;
		var start = all.indexOf('"display:none;">')+16;
		var end = all.indexOf('</div></div>');
		var error = all.substring(start,end);
		//ajax 响应下载必须要创建一个form实例
		if (!Ext.fly('formFly')) {  
		  var frm = document.createElement('form');  
		  frm.id = 'formFly';  
		  frm.className = 'x-hidden';  
		  document.body.appendChild(frm);  
		}  
		Ext.Ajax.request({  
		   disableCaching: true ,  
		   url : basePath+ 'excel/saveAsTxt.action',  
		   timeout: 100000000,  
		   method : 'post',  
		   isUpload: true,  
		   form: Ext.fly('formFly'),  
		   params : {  
			  error:error
		   }  
		}); 
	},
	help: function(){
		
	},
	isDebug: function() {
		return this.context.indexOf('_error_stack') > 0;
	}
});