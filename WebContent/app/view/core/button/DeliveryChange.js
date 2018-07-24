/**
 * 交期变更
 */	
Ext.define('erp.view.core.button.DeliveryChange',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDeliveryChangeButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: '交期变更',
    	id: 'erpDeliveryChangeButton',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
        menu: [{
			iconCls: 'main-msg',
	        text: '选择变更',
	        listeners: {
	        	click: function(m){ 
	        		Ext.getCmp('erpDeliveryChangeButton').deal('scm/changePurchaseNotifyDelivery.action', false, ['pn_date']);
	        	}
	        }
	    },{ 
	    	iconCls: 'main-msg',
	        text: '全部变更',
	        listeners: {
	        	click: function(m){
	        		Ext.getCmp('erpDeliveryChangeButton').deal('scm/changePurchaseNotifyDelivery.action', true, ['pn_date']);
	        	}
	        }
	    }],
		initComponent : function(){ 
			this.callParent(arguments); 
		},
	    /**
	     * @param useCondition	允许按条件执行 
	     * @param condParams	按条件执行时，作为额外条件传回的字段
	     */
	    deal: function(url, useCondition, condParams){
	    	var grid = Ext.getCmp('batchDealGridPanel');
	    	var items = grid.getMultiSelected();
	        Ext.each(items, function(item, index){
	        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
	        		item.index = this.data[grid.keyField];
	        		grid.multiselected.push(item);
	        	}
	        });
	    	var form = Ext.getCmp('dealform');
			var records = Ext.Array.unique(grid.multiselected);
			var bool = false, params = new Object(), data = new Array();
			params.caller = caller;
			if(!useCondition && records.length > 0){
				Ext.each(records, function(record, index){
					var f = form.fo_detailMainKeyField;
					if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
		        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) 
		        		||(f && this.data[f] != null && this.data[f] != ''
			        		&& this.data[f] != '0' && this.data[f] != 0)){
						bool = true;
						var o = new Object();
						if(grid.keyField){
							o[grid.keyField] = record.data[grid.keyField];
						} else {
							params.id[index] = record.data[form.fo_detailMainKeyField];
						}
						if(grid.toField){
							Ext.each(grid.toField, function(f, index){
								var v = Ext.getCmp(f).value;
								if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
									if(Ext.isDate(v)){
										v = Ext.Date.toString(v);
									}
									o[f] = v;
								}
							});
						}
						if(grid.necessaryFields){
							Ext.each(grid.necessaryFields, function(f, index){
								var v = record.data[f];
								if(Ext.isDate(v)){
									v = Ext.Date.toString(v);
								}
								o[f] = v;
							});
						}
						data.push(o);
					}
				});
				bool = true;
			} else{
				params.condition=null;
				if(useCondition) {
					 params.condition = form.getCondition();
				}
				bool = true; 
			}
			params.condParams=null;
			if(condParams) {
				var s = {};
				Ext.Array.each(condParams, function(p){
					var v = Ext.getCmp(p).getValue(); 
					if (v==null && useCondition){
						showError("必须选择变更交期!");
						bool=false;
					} 
					if(Ext.isDate(v)){
						v = Ext.Date.toString(v);
					}
					s[p] = v;
				});
				params.condParams = unescape(Ext.JSON.encode(s).replace(/\\/g,"%"));
			}
			if(bool){
				params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
				var main = parent.Ext.getCmp("content-panel");
				main.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + url,
			   		params: params,
			   		method : 'post',
			   		timeout: 1200000,
			   		callback : function(opt, s, r){
			   			main.getActiveTab().setLoading(false);
			   			var rs = new Ext.decode(r.responseText);
			   			if(rs.exceptionInfo){
			   				var str = rs.exceptionInfo;
			   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
			   					str = str.replace('AFTERSUCCESS', '');
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery();
			   				}
			   				showError(str);return;
			   			} else if(rs.success){
		    				if(rs.log){
		    					showMessage("提示", localJson.log);
		    				}
			   				Ext.Msg.alert("提示", "处理成功!", function(){
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery();
			   				});
			   			}
			   		}
				});
			}
	    }
	});