/**
 * 前工作中心提交批量过账
 */
Ext.define('erp.view.core.button.PCBatchPost',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPCBatchPostButton',
		param: [],
		id: 'erpPCBatchPostButton',
		//text: $I18N.common.button.erpPCBatchPostButton,
		text : '批量过账',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 100,
    	gridUtil : Ext.create('erp.util.GridUtil'),
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){
			this.callParent(arguments); 
		},
		handler : function(btn){
			var me = this;
			var panel = btn.ownerCt.ownerCt;
			var grid = Ext.getCmp('batchDealGridPanel');
			var datas = grid.getSelectionModel().getSelection();
			if(!datas.length>0){
				showError("请勾选需要的明细!");
				return;
			}
			var array = new Array();
			Ext.Array.each(datas,function(data){
				array.push(data.data);
			});//caller=Stepio!CraftTransfer
			if(array.length>0){
				grid.setLoading(true);
				Ext.Ajax.request({
					url:basePath + 'pm/make/batchPostStepio.action',
					params : {
						datas : Ext.encode(array),
						caller : 'BeforeCenterCommit'
					},
					method : 'post',
					callback : function(opt, s, r){
						grid.setLoading(false);
						var result = Ext.decode(r.responseText);
						if(result.success){
							showError("处理成功");
							/*var form = Ext.getCmp('dealform').getForm();
		    				var mc_wccode = form.getValues()["mc_wccode"];
		    				var condition = " si_status='已提交' and st_class in ('工序跳转','工序转移') and st_inwccode='"+mc_wccode+"' ";
		    				var param = {caller:'BeforeCenterCommit',condition:condition};
		    				me.gridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', param);*/
						}
						if(result.exceptionInfo){
							showError(result.exceptionInfo);
						}
					}
				});
			}
		}
	});