/**
 * 商机重启
 */	
Ext.define('erp.view.core.button.BusinessChanceRestart',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBusinessChanceRestartButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'businesschancerestartbtn',
    	text: '商机重启',
    	style: {
    		marginLeft: '10px'
        },
        width: null,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function(){
			var me = this;
			warnMsg('确认要重启选择的商机?', function(btnMsg){
				if(btnMsg == 'yes'){
					var grid = Ext.getCmp('batchDealGridPanel');
			        var items = grid.selModel.getSelection();
			        grid.multiselected = new Array();
			        Ext.each(items, function(item, index){
			        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
			        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
			        		grid.multiselected.push(item);
			        	}
			        });
					var records = grid.multiselected;
					if(records.length > 0){
						var id = new Array();
						var bcd_id = new Array();
						var bcdIdArr = '';
						Ext.each(records, function(record, index){
							id.push(record.data['bc_id']);
						});
						Ext.Ajax.request({
					   		url : basePath + 'crm/chance/businessChanceRestart.action',
					   		params: {
					   			ids: id.join(","),
					   			caller:caller
					   		},
					   		method : 'post',
					   		callback : function(options,success,response){
					   			grid.setLoading(false);
					   			var localJson = new Ext.decode(response.responseText);
					   			if(localJson.exceptionInfo){
					   				showError(localJson.exceptionInfo);
					   			} else {
					   				if(localJson.success){
					   					grid.multiselected = new Array();
					   					Ext.getCmp('dealform').onQuery(true);
					   				}
					   			}
					   		}
						});
					} else {
						showError("请勾选需要的明细!");
					}
				}
			});
		}
	});