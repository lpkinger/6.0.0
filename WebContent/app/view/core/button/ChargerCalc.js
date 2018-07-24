/**
 * 费用计算
 */
Ext.define('erp.view.core.button.ChargerCalc',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpChargerCalcButton',
		param: [],
		id: 'erpChargerCalcButton',
		text: $I18N.common.button.erpChargerCalcButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
        handler: function(){
        	var pick_date=Ext.getCmp('sa_flowdate');//计划提货日期
        	var sa_deposit = Ext.getCmp('sa_deposit').value;//本次冲定金
        	if(sa_deposit==null || sa_deposit==""){
        		sa_deposit = 0;
        	}
        	var idStr ='';
        	if(pick_date&&pick_date.value){
        		var form=Ext.getCmp('dealform'), grid = Ext.getCmp('batchDealGridPanel');
        		var items = grid.getMultiSelected();
        		if(items.length>0){
		        	Ext.each(items, function(item, index){
			        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
			        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
			        		item.index = this.data[grid.keyField];
			        		grid.multiselected.push(item);        		
			        	}
			        });
       			}
       			var records = Ext.Array.unique(grid.multiselected);
       			if(records.length > 0){
       				var data = new Array();
					var f = form.fo_detailMainKeyField;
       				Ext.each(records, function(record, index){
       					var o = new Object();
       					if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        				&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) 
	        				||(f && this.data[f] != null && this.data[f] != ''
		        			&& this.data[f] != '0' && this.data[f] != 0)){
		        				o[grid.keyField]=this.data[grid.keyField];
		        				o['sd_tqty']=this.data['sd_tqty'];
				    			//idStr = idStr+this.data[grid.keyField]+',';
		        		}
		        		data.push(o);
       				});
       			}
		    	if(data.length==0){
			  		showError('未勾选任何明细');
			  	}else{
				  	form.setLoading(true);
				    Ext.Ajax.request({
				    	url : basePath +'scm/sale/chargerCalc.action',
						params: {
							data:unescape(Ext.JSON.encode(data).replace(/\\/g,"%")),//idStr.substring(0,idStr.length-1),
							pickdate:pick_date.value,
							sa_deposit : sa_deposit,
							caller:caller
						},
						method : 'post',
						timeout: 360000,
						callback : function(options,success,response){
							form.setLoading(false);
							var res = new Ext.decode(response.responseText);
							if(res.success){
								//if(res.fee){
									Ext.getCmp('sa_totalupper').setValue(res.fee);
									Ext.getCmp('sa_totalupper').setFieldStyle({color:'#ff0000','font-weight':'bold'});
									Ext.getCmp('sa_realamount').setValue(res.amount);
									Ext.getCmp('sa_realamount').setFieldStyle({color:'#ff0000','font-weight':'bold'});
									Ext.getCmp('sa_total').setValue(res.total);
									Ext.getCmp('sa_total').setFieldStyle({color:'#ff0000','font-weight':'bold'});
									Ext.getCmp('sa_chargerate_user').setValue(res.chargerate);
									Ext.getCmp('sa_chargerate_user').setFieldStyle({color:'#ff0000','font-weight':'bold'});
									Ext.getCmp('sa_kcdjsfy_user').setValue(res.degree);
									Ext.getCmp('sa_kcdjsfy_user').setFieldStyle({color:'#ff0000','font-weight':'bold'});
									Ext.getCmp('sa_xyjsbzzd_user').setValue(res.xyjsbzzd);
									Ext.getCmp('sa_xyjsbzzd_user').setFieldStyle({color:'#ff0000','font-weight':'bold'});
									Ext.getCmp('sa_usedays').setValue(res.N);
									Ext.getCmp('sa_usedays').setFieldStyle({color:'#ff0000','font-weight':'bold'});
									Ext.getCmp('sa_taxtotal').setValue(res.thisamount);
									Ext.getCmp('sa_taxtotal').setFieldStyle({color:'#ff0000','font-weight':'bold'});
									Ext.getCmp('sa_prepayamount').setValue(res.yshkye);
									Ext.getCmp('sa_prepayamount').setFieldStyle({color:'#ff0000','font-weight':'bold'});
								//}
							}else if(res.exceptionInfo){
								var str = res.exceptionInfo;
								showError(str);return;
							}
						}
			    	});
		  		}
        	}else{
        		showError('请选择计划提货日期');
        	}        	
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});