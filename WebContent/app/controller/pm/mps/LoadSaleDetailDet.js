Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.LoadSaleDetailDet', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'common.batchDeal.Viewport','common.batchDeal.Form','common.batchDeal.GridPanel','core.button.AllThrow',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField',
     		'core.grid.YnColumn'
     	],
    FormUtil:Ext.create('erp.util.FormUtil'), 	
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({ 
    		'erpBatchDealFormPanel': {
    			alladded: function(form){
    				var grid = Ext.getCmp('batchDealGridPanel');
    				me.resize(form, grid);
    			}
    		},
    		'erpBatchDealGridPanel': {
    			afterrender: function(grid){
    				var form = Ext.getCmp('dealform');
    				me.resize(form, grid);
    			    if(type=='MPS') grid.defaultCondition+=Ext.isEmpty(grid.defaultCondition)?" sd_id not in (select md_sdid from mpsdetail where md_mainid="+keyValue+")":" AND sd_id not in (select md_sdid from mpsdetail where md_mainid="+keyValue+")";
    			    else if (type=='APS')  grid.defaultCondition+=Ext.isEmpty(grid.defaultCondition)?" sdd_id not in (select ad_sddid from apsdetail where ad_amid="+keyValue+")":" AND sdd_id not in (select ad_sddid from apsdetail where ad_amid="+keyValue+")";
    			}
    		},
    		'erpVastDealButton': {
    			click: function(btn){
    			    me.vastDeal(btn.ownerCt.ownerCt.dealUrl);	
    				me.BaseUtil.getActiveTab().close();
    			}
    		},
    		'erpVastAnalyseButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpVastPrintButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpVastAllotButton':{
    			click:function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpEndCRMButton':{
    			click:function(btn){
    				me.vastDeal('crm/chanceTurnEnd.action');
    			}
    		},
    		'erpAllThrowButton':{
    			beforerender:function(btn){
    				btn.text ="全部装载";
    				btn.handler=function(){
    					var condition=Ext.getCmp('dealform').getCondition();
    					 me.ThrowBycondition();
    				};
    			}
    		},
    		'condatefield[id=sdd_delivery]':{
    			afterrender:function(date){
    				date.combo.setRawValue('自定义');   	
    			}
    		},
    		'monthdatefield': {
				afterrender: function(f) {
					var type = '', con = null;
					if(f.name == 'vo_yearmonth' && caller == 'Voucher!Audit!Deal') {
						type = 'MONTH-A';
						con = Ext.getCmp('condatefield');
					} else if(f.name == 'vo_yearmonth' && caller == 'Voucher!ResAudit!Deal') {
						type = 'MONTH-A';
						con = Ext.getCmp('condatefield');
					}
					if(type != '') {
						this.getCurrentMonth(f, type, con);
					}
				}
			}
    	});
    },
    resize: function(form, grid){
    	if(!this.resized && form && grid && form.items.items.length > 0){
			var height = window.innerHeight;
			var cw = 0;
			Ext.each(form.items.items, function(){
				if(!this.hidden && this.xtype != 'hidden') {
					cw += this.columnWidth;
				}
			});
			cw = Math.ceil(cw);
			if(cw == 0){
				cw = 5;
			} else if(cw > 2 && cw <= 5){
				cw -= 1;
			} else if(cw > 5 && cw < 8){
				cw = 4;
			}
			cw = Math.min(cw, 5);
			form.setHeight(height*cw/10 + 10);
			grid.setHeight(height*(10 - cw)/10 - 10);
			this.resized = true;
		}
    },
    countGrid: function(){
    	//重新计算合计栏值
    	var grid = Ext.getCmp('batchDealGridPanel');
    	Ext.each(grid.columns, function(column){
			if(column.summary){
				var sum = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						sum += Number(item.value);
					}
				});
				Ext.getCmp(column.dataIndex + '_sum').setText(column.text + '(sum):' + sum);
			} else if(column.average) {
				var average = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						average += Number(item.value);
					}
				});
				average = average/grid.store.data.items.length;
				Ext.getCmp(column.dataIndex + '_average').setText(column.text + '(average):' + average);
			} else if(column.count) {
				var count = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						count++;
					}
				});
				Ext.getCmp(column.dataIndex + '_count').setText(column.text + '(count):' + count);
			}
		});
    },
    ThrowBycondition:function(){
		var condition=Ext.getCmp('dealform').getCondition();
		var me=this;
		me.FormUtil.setLoading(true);		
		var params=new Object();
		params.keyValue=keyValue;
		params.type=type;
		params.condition=condition;
		 
		Ext.Ajax.request({
	   		url : basePath + "pm/mps/loadSaleDetailDet.action",
	   		params: params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   				return "";
	   			}
    			if(localJson.success){
    				if(localJson.log){
    					showMessage("提示", localJson.log);
    				}
	   				Ext.Msg.alert("提示", "装载成功!", function(){ 
	   					Ext.getCmp('dealform').onQuery();
	   				});
	   			}
	   		}
		});  
	},
    vastDeal: function(url){
    	var grid = Ext.getCmp('batchDealGridPanel');
        var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		item.index = this.data[grid.keyField];
        		grid.multiselected.push(item);
        	}
        });
    	var form = Ext.getCmp('dealform');
		var records = Ext.Array.unique(grid.multiselected);
		if(records.length > 0){
			var params = new Object();
			params.caller = caller;
			var data = new Array();
			var bool = false;
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
			if(bool){
				params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
				params.keyValue=keyValue;
				params.type=type;
				var main = parent.parent.Ext.getCmp("content-panel");
				main.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + url,
			   		params: params,
			   		method : 'post',
			   		timeout: 6000000,
			   		callback : function(options,success,response){
			   			main.getActiveTab().setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				var str = localJson.exceptionInfo;
			   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
			   					str = str.replace('AFTERSUCCESS', '');
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery();
			   				}
			   				showError(str);return;
			   			}
		    			if(localJson.success){
		    				if(localJson.log){
		    					showMessage("提示", localJson.log);
		    				}
			   				Ext.Msg.alert("提示", "处理成功!", function(){
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery();
			   				});
			   			}
			   		}
				});
			} else {
				showError("没有需要处理的数据!");
			}
		}
    },
    getCurrentMonth: function(f, type, con) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: type
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				f.setValue(rs.data.PD_DETNO);
    				if(con != null) {
    					con.setMonthValue(rs.data.PD_DETNO);
    				}
    			}
    		}
    	});
    }
});