/**
 * 本工作中心移交批量打印
 */
Ext.define('erp.view.core.button.PCBatchCommit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPCBatchCommitButton',
		param: [],
		id: 'erpPCBatchCommitButton',
		text: '批量提交',
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
			//var grid = panel.down('#ThisCenterCommit');
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
					url:basePath + 'pm/make/batchSumbitStepio.action',
					params : {
						datas : Ext.encode(array),
						caller : 'ThisCenterCommit'
					},
					method : 'post',
					callback : function(opt, s, r){
						grid.setLoading(false);
						var result = Ext.decode(r.responseText);
						if(result.success){
							showError("处理成功");
							/*var form = Ext.getCmp('dealform').getForm();
		    				var mc_wccode = form.getValues()["mc_wccode"];
		    				var condition = " si_status='在录入' and st_outwccode='"+mc_wccode+"' ";
		    				var param = {caller:'ThisCenterCommit',condition:condition};
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