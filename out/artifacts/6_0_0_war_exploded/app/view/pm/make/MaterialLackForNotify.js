Ext.QuickTips.init();
Ext.define('erp.controller.common.BatchDeal', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'common.batchDeal.Viewport','common.batchDeal.Form','common.batchDeal.GridPanel',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField',
     		'core.grid.YnColumn'
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
    		'erpVastDealButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
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
			if('common/form/vastPost.action' == url) {
				this.vastPost(grid, records, url);
				return;
			}
			var params = new Object();
			params.id=new Array();
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
				params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));;
				var main = parent.Ext.getCmp("content-panel");
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
    },
    vastPost: function(grid, records, url) {
    	var me = this, win = Ext.getCmp('win-post');
    	grid._postrecords = records;
    	if(!win) {
    		win = Ext.create('Ext.Window', {
    			id: 'win-post',
    			width: '70%',
    			height: '60%',
    			modal: true,
    			layout: 'anchor',
    			items: [{
    				xtype: 'form',
    				anchor: '100% 100%',
    				bodyStyle: 'background: #f1f1f1;',
    				layout: 'column',
    				defaults: {
    					xtype: 'checkbox',
    					margin: '2 10 2 10',
    					columnWidth: .33
    				},
    				items: [{
    					xtype: 'displayfield',
    					fieldLabel: '当前账套',
    					id: 'ma_name'
    				},{
    					xtype: 'displayfield',
    					fieldLabel: '账套描述',
    					margin: '2 10 30 10',
    					id: 'ma_function',
    				},{
    					xtype: 'displayfield',
    					fieldLabel: '目标账套',
    					columnWidth: 1
    				},{
    					boxLabel: '全选',
    					columnWidth: 1,
    					listeners: {
    						change: function(f) {
    		    				var form = f.up('form');
    		    				form.getForm().getFields().each(function(a){
    		    					if(a.xtype == 'checkbox' && a.id != f.id) {
    		    						a.setValue(f.value);
    		    					}
    		    				});
    		    			}
    					}
    				}]
    			}],
    			buttonAlign: 'center',
    			buttons: [{
					text: $I18N.common.button.erpConfirmButton,
					cls: 'x-btn-blue',
					handler: function(btn) {
						var w = btn.ownerCt.ownerCt, form = w.down('form'),
							from = form.down('#ma_name').value,
							items = form.query('checkbox[checked=true]'),
							data = new Array();
						Ext.each(items, function(item){
							if (item.ma_name)
								data.push(item.ma_name);
						});
						if(data.length > 0)
							me.post(w, grid, url, from, data.join(','));
					}
				},{
					text: $I18N.common.button.erpCloseButton,
					cls: 'x-btn-blue',
					handler: function(btn) {
						btn.ownerCt.ownerCt.hide();
					}
				}]
    		});
    		this.getMasters(win);
    	}
    	win.show();
    },
    post: function(w, grid, url, from, to) {
    	var records = grid._postrecords;
    	w.setLoading(true);
		var d = new Array(), f = grid.keyField;
		Ext.each(records, function(r) {
			d.push(r.get(f));
		});
		Ext.Ajax.request({
			url: basePath + url,
			params: {
				caller: caller,
				data: d.join(','),
				to: to
			},
			callback: function(opt, s, r) {
				w.setLoading(false);
				if(s) {
					var rs = Ext.decode(r.responseText);
					if(rs.data) {
						showMessage('提示', rs.data);
					} else {
						alert('抛转成功!');
					}
					grid.multiselected = new Array();
					grid._postrecords = null;
   					Ext.getCmp('dealform').onQuery();
   					w.hide();
				}
			}
		});
    },
	/**
	 * 加载系统所有账套
	 */
	getMasters: function(win){
		Ext.Ajax.request({
			url: basePath + 'common/getAbleMasters.action',
			method: 'get',
			callback: function(opt, s, res){
				var r = Ext.decode(res.responseText), c = r.currentMaster;
				if(r.masters){
					var form = win.down('form'), items = new Array();
    				for(var i in r.masters) {
    					var d = r.masters[i];
    					if(d.ma_name != c) {
    						if(d.ma_type == 3) {
    							var o = {boxLabel: d.ma_name + '(' + d.ma_function + ')', ma_name: d.ma_name};
            					items.push(o);
    						}
    					} else {
    						form.down('#ma_name').setValue(c);
    						form.down('#ma_function').setValue(d.ma_function);
    					}
    				}
    				form.add(items);
				}
			}
		});
	}
});