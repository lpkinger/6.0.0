Ext.QuickTips.init();
Ext.define('erp.controller.pm.BatchDeal', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'pm.Viewport','common.batchDeal.Form','common.batchDeal.GridPanel','core.trigger.AddDbfindTrigger','core.button.CheckCustomerUU',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.button.TurnMeetingButton','core.trigger.MultiDbfindTrigger',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.trigger.SchedulerTrigger','core.button.PCBatchPrint','core.button.PCBatchCommit','core.button.PCBatchPost',
     		'core.grid.YnColumn','common.datalist.GridPanel','core.button.ProcessTransfer','core.form.DateHourMinuteField','core.form.SeparNumber','core.grid.YnColumnNV','core.button.DeblockSplit','core.button.HandLocked','core.button.BusinessChanceLock','core.button.BusinessChanceRestart'
     		],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({
    		'erpBatchDealFormPanel': {
    			alladded: function(form){
    				var grid = Ext.getCmp('batchDealGridPanel');
    				me.resize(form, grid);
    				var items = form.items.items, autoQuery = false;
					Ext.each(items, function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val)) {
							this.setValue(val);
							autoQuery = true;
							if(this.xtype == 'dbfindtrigger') {
								this.autoDbfind('form', caller, this.name, this.name + " like '%" + val + "%'");
							}
						}
					}); 
					if(!form.tempStore){
						grid.columns[1].hide();
					}
					if(autoQuery) {
						setTimeout(function(){
							form.onQuery();
						}, 1000);
					}
					if(form.source=='allnavigation'){
        				Ext.each(form.dockedItems.items[0].items.items,function(btn){
        					btn.setDisabled(true);
        				});
        			}
    			}  			
    		},
       		'#mc_wccode' : {
    			aftertrigger : function(field){}
    				/*var st_inwccode = field.value;
    				//前工作中心提交
    				var grid = Ext.getCmp('BeforeCenterCommit');
    				var condition = " si_status='已提交' and st_class in ('工序跳转','工序转移') and st_inwccode='"+st_inwccode+"' ";
    				var param = {caller:'BeforeCenterCommit',condition:condition};
    				me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', param);
    				//前工作中心在制
    				grid = Ext.getCmp('BeforeCenterMake');
    				condition = " mc_status in ('待完工','已完工','已审核')  and mc_wccode='"+st_inwccode+"' ";
    				param = {caller:'BeforeCenterMake',condition:condition};
    				me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', param);
    				//后工作中心确认
    				grid = Ext.getCmp('AfterCenterConfirm');
    				condition = " st_class in ('工序跳转','工序转移') and  si_status='已提交' and  st_outwccode='"+st_inwccode+"' ";
    				param = {caller:'AfterCenterConfirm',condition:condition};
    				me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', param);
    				//本工作中心移交
    				grid = Ext.getCmp('ThisCenterCommit');
    				condition = " si_status='在录入' and st_outwccode='"+st_inwccode+"' ";
    				condition = " si_status='在录入' ";
    				param = {caller:'ThisCenterCommit',condition:condition};
    				me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', param);
    			}*/
    		},
    		/**
    		 * 本工作中心移交
    		 */
    		'#ThisCenterCommit' : {
    			afterrender : function(grid){
    				//var form = Ext.getCmp()
    				var form = Ext.getCmp('dealform').getForm();
    				var mc_wccode = form.getValues()["mc_wccode"];
    				var condition = " si_status='在录入' and st_outwccode='"+mc_wccode+"' ";
    				var param = {caller:'ThisCenterCommit',condition:condition};
    				me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', param);
    			}
    		},
    		/**
    		 * 前工作中心提交
    		 */
    		'#BeforeCenterCommit':{
    			afterrender : function(grid){
    				var form = Ext.getCmp('dealform').getForm();
    				var mc_wccode = form.getValues()["mc_wccode"];
    				var condition = " si_status='已提交' and st_class in ('工序跳转','工序转移') and st_inwccode='"+mc_wccode+"' ";
    				var param = {caller:'BeforeCenterCommit',condition:condition};
    				me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', param);
    			}
    		},
    		/**
    		 * 前工作中心在制
    		 */
    		'#BeforeCenterMake':{
    			afterrender : function(grid){
    				var form = Ext.getCmp('dealform').getForm();
    				var mc_wccode = form.getValues()["mc_wccode"];
    				//var condition = " mc_status in ('待完工','已完工','已审核') and mc_wccode='"+mc_wccode+"'";
    				var condition = " mc_tasktype='车间作业单'  and mc_nextwccode='"+mc_wccode+"' ";
    				var param = {caller:'BeforeCenterMake',condition:condition};
    				me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', param);
    			}
    		},
    		/**
    		 * 后工作中心确认
    		 */
    		'#AfterCenterConfirm':{
    			afterrender : function(grid){
    				var form = Ext.getCmp('dealform').getForm();
    				var mc_wccode = form.getValues()["mc_wccode"];
    				var condition = " st_class in ('工序跳转','工序转移') and  si_status='已提交' and st_outwccode='"+mc_wccode+"'";
    				var param = {caller:'AfterCenterConfirm',condition:condition};
    				me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', param);
    			}
    		},
    		'erpProcessTransferButton':{//工序转移
    			click:function(btn){
    				me.vastDeal('pm/mes/vastTurnCraftTransfer.action');
    			}
    		},
    		'erpProcessForwardButton':{//工序跳转
    			click:function(btn){
    				me.vastDeal('pm/mes/vastTurnCraftJump.action');
    			}
    		},
    		'erpCompletingStoreButton':{//完工入库
    			click:function(btn){
    				me.vastDeal('pm/mes/vastTurnMadeIN.action');
    			}
    		},
    		'erpProcessReturnButton':{//工序退制 
    			click:function(btn){
    				me.vastDeal('pm/mes/vastTurnCraftBack.action');
    			}
    		},
    		'erpProcessMateriaButton':{//工序退料
    			click:function(btn){
    				me.vastDeal('pm/mes/vastTurnCraftReturn.action');
    			}
    		},
    		'erpProcessBadButton':{//工序报废
    			click:function(btn){
    				me.vastDeal('pm/mes/vastTurnCraftScrap.action');
    			}
    		}
    	});
    },
    resize: function(form, grid){
    	if(!this.resized && form && grid && form.items.items.length > 0){
    		var height = window.innerHeight, 
				fh = form.getEl().down('.x-panel-body>.x-column-inner').getHeight();
			form.setHeight(35 + fh);
			grid.setHeight(height - fh - 35);
			this.resized = true;
		}
    },
    vastDeal: function(url){		
		var grid = Ext.getCmp('batchDealGridPanel');
        var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		var bool = true;
        		Ext.each(grid.multiselected, function(){
        			if(this.data[grid.keyField] == item.data[grid.keyField]){
        				bool = false;
        			}
        		});
        		if(bool){
        			grid.multiselected.push(item);
        		}
        	}
        });
    	var form = Ext.getCmp('dealform');
		var records = grid.multiselected; 
		if(records.length!=1){//通达老系统时限制，之后需要优化
			showError("只能选择一个工序!");
			grid.multiselected = new Array();
		}else if(records.length > 0 ){
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
							o[f] = record.data[f];
						});
					}
					data.push(o);
				}
			});
			if(bool){
				params.data = Ext.encode(data); 
				grid.setLoading(true);
				Ext.Ajax.request({
			   		url : basePath +url,
			   		params: params,
			   		method : 'post',
			   		callback : function(options,success,response){
			   			grid.setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				showError(localJson.exceptionInfo);
			   			} else {
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
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery(true);
			   				}
			   			}
			   		}
				});
			}else {
				showError("没有需要处理的数据!");
			}
		}else {
			showError("请勾选需要的明细!");
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
    }
});