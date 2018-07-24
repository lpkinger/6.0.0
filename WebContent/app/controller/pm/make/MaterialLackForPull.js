Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MaterialLackForPull', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'common.batchDeal.Viewport','common.batchDeal.Form','pm.mps.MRPThrowGrid',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField',
     		'core.grid.YnColumn','core.button.WipSend'
     	],
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
    			}
    		}, 
    		'button[id=close]':{
	    		afterrender:function(btn){
	    			btn.ownerCt.insert(2,{
	      				  xtype:'erpWipSendButton'
	      			  }); 
	       		 }
				
    		},
    		'button[id=WipSend]': {
    			click:function(btn){     
    				me.toLSSend('pm/wcplan/throwwipneed.action');
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
    toLSSend: function(url){ 
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
        var params = new Object();  
		var records = grid.multiselected;
		if(records.length > 0){
			if(records.length > 500) {
				showMessage('提示', '勾选行数必须小于500条!');
				return;
			}
			var data = new Array(); 
			Ext.each(records, function(record, index){ 
				dd=new Object();
			   	dd['mlp_id']=record.data['mlp_id']; 
				data.push(dd);
			});
			params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
			grid.setLoading(true);
			Ext.Ajax.request({
		   		url : basePath + url,
		   		params: params,
		   		method : 'post',
		   		callback : function(options,success,response){
		   			grid.setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   			} else if(localJson.log){
		   				showMessage('提示', localJson.log);
		   				Ext.getCmp('dealform').onQuery(true);
		   			}
		   		}
			});
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