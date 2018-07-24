Ext.define('erp.view.b2c.main.help.Help', {
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	cls:'Welcome-Viewport',
	bodyStyle:'background:#FFFFFF !important;',
	initComponent : function(){ 
		var me = this; 
		me.BaseUtil = Ext.create('erp.util.BaseUtil');
		me.TITLE = me.BaseUtil.getUrlParam("TITLE");
		me.MAXCARD = me.BaseUtil.getUrlParam("MAXCARD");
		me.bgimg = me.BaseUtil.getUrlParam("imgs");
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
						//text:'上一页',
						cls:'prev',
						width:125,
						height:84
				}],
				rbar:[{
						id:'next',
						xtype:'button',
						cls:'next',
						//text:'下一页',
						width:125,
						height:147
				}],
				activeItem: 0,
				items:[]
			}]
		});
		for(var i = 1;i<me.MAXCARD+1;i++){
			me.items[0].items.push({
				id: "c"+i,
				html: '<div align="center" id="w'+i+'" style=" background: url(&quot;'+helpPaths+i+'.'+me.bgimg+'.png&quot;) center no-repeat" class="helpImage default-panel"><div class="welcomenew">'+me.TITLE+'</div></div>' 
			});
		}
		me.callParent(arguments);
	}
});