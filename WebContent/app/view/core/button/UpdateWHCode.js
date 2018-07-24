/**
 * 修改仓库按钮
 */	
Ext.define('erp.view.core.button.UpdateWHCode',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpUpdateWHCodeButton',
	iconCls: 'x-button-icon-submit',
	cls: 'x-btn-gray',
	text: $I18N.common.button.erpUpdateWHCodeButton,
	style: {
		marginLeft: '10px'
	},
	initComponent : function(){ 
		this.callParent(arguments); 
	},
	listeners: {
		afterrender: function(btn) {
			var status = Ext.getCmp('pi_statuscode');
			if(status && status.value == 'POSTED'){
				btn.hide();
			}
		},
		click:function(){
			var whcode=Ext.getCmp('pi_whcode');
			var inwhcode=Ext.getCmp('pi_purpose');
			var piid = Ext.getCmp('pi_id').value;
			if(whcode && whcode.value){
				var codevalue=whcode.value;
				var value=Ext.getCmp('pi_whname').getValue();
				var inwhname=Ext.getCmp('pi_purposename');
				if(caller=='ProdInOut!AppropriationOut' &&  (!inwhcode || !inwhcode.value)){
					 Ext.Msg.alert('提示','请选择对应拨出拨入仓库!');
				}
				Ext.MessageBox.show({
					title:'确认修改?',
					msg: '确认要将单据主记录的仓库:'+value+'更新到所有明细行中吗？',
					buttons: Ext.Msg.YESNO,
					icon: Ext.Msg.WARNING,
					fn: function(btn){
						if(btn == 'yes'){
							//保存  
							var grid=Ext.getCmp('grid');
							grid.setLoading(true);
							Ext.Ajax.request({
								url:basePath+'scm/reserve/updateDetailWH.action',
								method:'post',
								params:{
									pi_id:piid,
									codevalue:codevalue,
									value:value,
									pd_inwhcode:inwhcode==null?"":inwhcode.value,
									pd_inwhname:inwhname==null?"":inwhname.value,
									caller:caller
								},
								callback:function(options,success,response){
									grid.setLoading(false);
									var res = Ext.decode(response.responseText);
									if(res.success){
										showError("更新成功！");
										window.location.reload();
									}
								}
							});
						} else if(btn == 'no'){
							//不保存	
							return;
						} else {
							return;
						}
					}
				});
			}else {
		      Ext.Msg.alert('提示','请选择仓库!');

			}
		}}
});