/**
 * 转拨出单按钮
 */	
Ext.define('erp.view.core.button.TurnAppropriate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnAppropriateButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnAppropriateButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler:function(btn){
			var form = btn.ownerCt.ownerCt;
			var status = Ext.getCmp(form.statuscodeField);
			var me = this;
			var id = Ext.getCmp(form.keyField).value;
			if(status && status.value != 'AUDITED'){
				showError('只有【已审核】状态的销售订单才允许转拨出单');
				return;
			}
			var need = Ext.getCmp('sa_need1').value;
			if(need && need =='已转拨出单'){
				showError('销售订单已转拨出单，不允许重复转单');
				return;
			}
			Ext.Ajax.request({
				url : basePath + 'scm/sale/turnPage.action?caller=' +caller,
				params: {
					id: id,
					data:'Sale'
				},
				method : 'post',
				callback : function(options,success,response){
					me.setLoading(false);
					var localJson = new Ext.decode(response.responseText);
					if(localJson.success){
						//audit成功后刷新页面进入可编辑的页面 
						window.location.reload();
					} else {
						if(localJson.exceptionInfo){
							var str = localJson.exceptionInfo;
							if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
								str = str.replace('AFTERSUCCESS', '');
								me.getMultiAssigns(id, caller, form,me.showAssignWin);
							} 
							showMessage("提示", str);
						}
					}
				}
			});
		}
	});