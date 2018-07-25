Ext.define('erp.view.b2c.main.Welcome', {
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	cls:'Welcome-Viewport',
	bodyStyle:'background:#FFFFFF !important;',
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
			items: [{
			    region: 'center', 
			    width : '100%',
			    height:'100%',
				xtype: 'panel',
				id:'descPanel',
				layout : 'card',
				border:false,
				frame:true,
				defaults:{
					border: false,
					bodyStyle: "padding:0px; background-color: #ccc",
					width:'100%',
					height:'100%'
				},
				lbar:[{
						id:'prev',
						xtype:'button',
						cls:'prev',
						width:125,
						height:84
				}],
				rbar:[{
						id:'next',
						xtype:'button',
						cls:'next',
						width:125,
						height:147
				}],
				activeItem: 0,
				items:[]
			}]
		});
		for(var i = 1;i<MAXCARD+1;i++){
			me.items[0].items.push({
				id: "c"+i,
				html: '<div align="center" id="w'+i+'"  class="helpImage default-panel"><div class="welcomenew">'+TITLE+'</div></div>', 
				items:[{
			   		cls:'welcome-colse',
			   		xtype:'button',
			   		border:0,
			   		listeners: {
			   			click: function(btn) {
			   				if(parent.window.Ext.getCmp('twin_2017000000')){
			   					parent.window.Ext.getCmp('twin_2017000000').close();
			   				}
						},afterrender:function(btn){
							if(!parent.window.Ext.getCmp('twin_2017000000')){
								btn.hide();
							}
						}
			   		}
			   	}]
			});
		}
		me.callParent(arguments);
	}
});