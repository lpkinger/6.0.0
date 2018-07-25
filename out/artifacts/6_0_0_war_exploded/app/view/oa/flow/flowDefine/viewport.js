Ext.define('erp.view.oa.flow.flowDefine.viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				tbar:{cls:'x-flow-tbar',items:[{
					xtype:'button',
					id:flowCaller?'updateFlowDefine':'saveFlowDefine',
					cls:'x-btn-gray',
					text:flowCaller?'更新':'保存',
					formBind:true
				},{
					xtype:'button',
					cls:'x-btn-gray',
					margin:'0 0 0 5',
					text:'取消',
					handler:function(b){
						var errInfo = '是否关闭界面？';
						warnMsg(errInfo, function(btn){
							if(btn == 'yes'){
								var main = parent.Ext.getCmp("content-panel"),bool = false; 
								if(main){
									bool = true;
									main.getActiveTab().close();
								} else {
									var win = parent.Ext.ComponentQuery.query('window');
									if(win){
										Ext.each(win, function(){
											this.close();
										});
									} else {
										bool = true;
										window.close();
									}
								}
								if(!bool){//如果还是没关闭tab，直接关闭页面
									window.close();
								}
							} else {
								return;
							}
						});
					}
				}]},
				title:'新建流程',
				cls:'flow_mainpanel',
				xtype:'flowDefineform',
				anchor:'100% 24%'
			},{
				region:'center',
				xtype:'formSet',
				anchor:'100% 76%'
			}]
		}); 
		me.callParent(arguments);
	}
});