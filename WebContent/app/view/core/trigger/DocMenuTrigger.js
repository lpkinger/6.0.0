/**
 * 文档归档设置取目录的trigger
 */
Ext.define('erp.view.core.trigger.DocMenuTrigger', {
	extend : 'Ext.form.field.Trigger',
	alias : 'widget.docMenuTrigger',
	triggerCls : 'x-form-autocode-trigger',
	afterrender : function() {
		this.addEvent({
					'aftertrigger' : true
				});
	},
	onTriggerClick : function(e) {
		if("DocSetting" == caller){
			this.showMenuWin();
		}
		if("uploadDocument" == caller){
			this.showMenuWin();
		}
	},
	showMenuWin : function(){
		var win = this.win;
		var type = this.type || this.getType();
		var trigger=this.id;
		if(!win){
			this.win = win = new Ext.window.Window({
				id : 'manuwin',
				height : "70%",
				width : "40%",
				maximizable : true,
				closeAction : 'hide',
				buttonAlign : 'center',
				autoScroll:true,
				layout : 'anchor',
				title : '目录选择',
				items : [{
					xtype: 'menuTree',
					anchor : '100% 93%',
					autoScroll:true,
					layout : 'fit'
				},{
					xtype: 'form',
					region: 'sourth',
					anchor : '100% 7%',
					layout: {
				        align: 'middle',
				        pack: 'center',
				        type: 'hbox'
					},
					items: [{
						xtype: 'textfield',
						name: 'manuValue',
						hidden: true,
						id: 'manuValue'
					},{
						xtype: 'textfield',
						name: 'prefixCode',
						hidden: true,
						id: 'prefixCode'
					},{
						xtype: 'button',
						text: '确认',
						iconCls: 'x-button-icon-submit',
				    	cls: 'x-btn-gray-1',
				    	id: 'confirmMenu',
					},{
						xtype: 'button',
						id: 'closeMenu',
						style: {marginLeft:'20px'},
						text: '关闭',
						iconCls: 'x-button-icon-close',
				    	cls: 'x-btn-gray-1',
					}]
				}]
			});
		}
		win.show();
	},
	
	getType : function() {
		var type = 'DocSetting';
		switch (caller) {
			case 'DocSetting':
				type = 'DocSetting';
				break;
			case 'uploadDocument':
				type = 'uploadDocument';
				break;
		}
		return type;
	}
	
});