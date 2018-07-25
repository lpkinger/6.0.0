Ext.define('erp.view.common.DeskTop.Portlet', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.portlet',
	layout: 'fit',
	anchor: '100%',
	frame: true,
	animCollapse: true,
	draggable: true,
	maxCount:50,
	cls: 'x-portlet',
	defaults:{
		cls:'custom-framed'
	},
	initComponent : function(){ 
		this.callParent(arguments);
		if(this.enableTools) {
			this.getTools();
		}
	},
	getTools:function(){
		var me=this;
		this.tools=[{
			type:'set',
			cls : 'set-tool',
			tooltip: '设置',
			listeners : {
				click : function(t) {
					Ext.MessageBox.show({
						title: '展示记录数设置',
						msg: '请输入记录数(<=50):',
						width:200,
						buttons: Ext.MessageBox.OKCANCEL,
						prompt: true,
						value:t.ownerCt.ownerCt.pageCount,
						animateTarget:t.getId(),
						fn:me.setPageCount,		
						updateXtype:t.ownerCt.ownerCt.xtype_,
						sourcePortal:t.ownerCt.ownerCt
					});
				}
			}
		},{
			type:'more',
			cls : 'more-tool',
			tooltip: '更多',
			listeners : {
				click : function(t) {
					me.getMore();
				}
			}
		}];
	},
	setPageCount:function(type,c,o){
		if(type=='ok'){
			if(Ext.isNumeric(c)){
				var portal=o.sourcePortal,itemPortal=portal.items.items[0];
				c = Math.round(c); //四舍五入
				if(portal && portal.maxCount>0 && (c>portal.maxCount || c<1 )){
					alert('记录数超出限制'); return;
				}else {
					Ext.Ajax.request({
						url:basePath+'/common/desktop/setTotalCount.action',
						method:'POST',
						params:{
							type:o.updateXtype,
							count:c
						},
						callback : function(options, success, response){
							var res = response.responseText;
							if(res=='success'){
								//更新成功
								portal.pageCount=c;
								if(itemPortal.xtype=='tabpanel'){
									var store=itemPortal.getActiveTab().getStore();
									var params=store.proxy.extraParams;
									Ext.apply(params,{
										count: c,
										pageSize:c
									});
									store.load();
								}else{
									var store=itemPortal.getStore();
									var params=store.proxy.extraParams;
									Ext.apply(params,{
										count: c,
										pageSize:c
									});
									store.load();
								}						 
							}					  
						}
					});	
				}
			}else alert('请输入正确的数字!');
		}
	},
	getMore:function(){}
});
