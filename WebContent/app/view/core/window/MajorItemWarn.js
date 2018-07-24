Ext.define('erp.view.core.window.MajorItemWarn', {
	extend: 'Ext.window.Window',
	alias: 'widget.majoritemwarn',
	id:'majoritemwarn',
	width: 595,
	height:400,
	frame: true,
	resizable:false,
	modal: true,
	bodyStyle: 'background: #E0EEEE;',
	layout: 'column',
	title: '',
	titlevalue: '',
	cvalue:'',
	closable : false,
	approver:'',
	apptime:'',
	itemContent:'',
	initComponent: function() {
		var me=this;
		this.title = '<div style="height:25;padding-top:5px;color:blue;font-size:16px;text-align:center;background: #E0EEEE url(' + 
			basePath + 'resource/ext/resources/themes/images/default/grid/grid-blue-hd.gif) repeat center center">&nbsp;&nbsp;</div>';
		this.titlevalue="<div style='text-align:center;font-size:20px'>"+this.titlevalue+"</div>";
		this.approver="<div style='float:right;width:40px;text-align:center'>"+this.approver+"</div>";
		this.apptime="<div style='float:right;'>"+this.apptime+"</div>";
		Ext.apply(me, { 
			items: [{
				xtype: 'form',
				anchor: '100% 100%',
				bodyStyle: 'background: #E0EEEE;',
				items: [{
					xtype: 'tbtext',
					scroll:false,		
					id:'title',		
					padding:'15 0 0 0 ',
					text:me.titlevalue
				},{
					xtype: 'tbtext',
					scroll:false,
					width:588,
					height:250,
					id:'itemContent',
					text:me.itemContent,
					padding: '20 0 0 40'
				},{
					xtype: 'tbtext',
					scroll:false,		
					id:'approver',
					text:me.approver
				},{
					xtype: 'tbtext',
					scroll:false,		
					id:'apptime',
					padding: '0 0 30 0',
					scroll:false,
					text:me.apptime
				}],
				buttonAlign: 'center',
				buttons: [{
					text: '关闭',
					cls: 'x-btn-blue',
					height: 25,
					width:65,
					handler: function(btn) {
						var win = btn.up('window');	
						win.close();
					}
				}]
			}]
		});
		this.callParent(arguments);
		this.show();
	}
});
