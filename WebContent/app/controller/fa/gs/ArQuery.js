Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.ArQuery', {
    extend: 'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
    condition:'',
    views:[
     		'fa.gs.arQuery.Viewport','fa.gs.arQuery.GridPanel','fa.gs.arQuery.QueryForm','fa.gs.arQuery.QueryWin',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.ConDateField','core.form.YnField',
     		'core.form.FtDateField','core.form.FtFindField','core.grid.YnColumn','core.grid.TfColumn','core.form.ConMonthDateField',
     		'core.button.Refresh'
     	],
    refs: [{ref: 'grid', selector: '#arquerygrid'}],
    init:function(){
    	var me = this;
    	this.control({
    		'erpQueryFormPanel button[name=confirm]': {
    			
    		},
    		'button[name=refresh]':{
    			click: function(btn){
    				me.getGrid().setLoading(true);
    				Ext.Ajax.request({
    					url: basePath + 'fa/gs/ArQueryController/refreshArQuery.action',
    					method: 'GET',
    					timeout: 120000,
    					callback: function(opt, s, r) {
    						me.getGrid().setLoading(false);
    						var rs = Ext.decode(r.responseText);
    						if(rs.success) {
    							//grid 刷新操作
    							me.query(me.condition);
    						}else{
    							//grid 刷新操作
    							me.query(me.condition);
    						}
    					}
    				});
    			}
    		},
    		'button[name=query]':{
    			afterrender: function(btn){
    				var me = this;
    				var filter = me.filter = me.createFilterPanel(btn);
    				filter.show();
    			},
    			click: function(btn){
    				var me = this;
    		    	if (me.filter){
    		    		me.filter.show();
    		    	} else{
    		    		var filter = me.filter = me.createFilterPanel(btn);
    		    		filter.show();
    		    	}
    				
    			}
    		}
    	});
    },
    
    createFilterPanel:function(btn){
    	var me = this;
    	var filter = Ext.create('Ext.Window', {
    		id: btn.getId() + '-filter',
    		style: 'background:#f1f1f1',
    		title: '筛选条件',
    		width: 500,
    		modal:true,
    		height: 385,
    	    layout: 'column',
    	    defaults: {
    	    	margin: '2 2 2 10'
    	    },
    	    items: [{
				id: 'am_yearmonth',
				name: 'am_yearmonth',
				xtype: 'conmonthdatefield',
				fieldLabel: '期间',
				labelWidth: 80,
				margin: '10 2 2 10',
				columnWidth: .51,
				getValue: function() {
					if(!Ext.isEmpty(this.value)) {
						return {begin: this.firstVal, end: this.secondVal};
					}
					return null;
				},
				listeners:{
					afterrender:function(cmd){
						me.getCurrentYearmonth(cmd);
					}
				}
			},{
				xtype: 'dbfindtrigger',
				fieldLabel: '币别',
				height: 23,
				labelWidth: 80,
				id: 'am_currency',
				name:'am_currency',
				margin: '10 2 2 10',
				flex: 0.2,
				columnWidth: .51
				
			},{

				fieldLabel: '账户编号',
				labelWidth: 80,
				height: 23,
				layout: 'hbox',
				columnWidth: 1,
				xtype: 'fieldcontainer',
				id: 'amq_accountcode',
				defaults: {
					fieldStyle : "background:#FFFAFA;color:#515151;"
				},
				items: [{
					labelWidth: 35,
					xtype: 'dbfindtrigger',
					flex: 0.32,
					id: 'ca_code',
					name: 'ca_code'
				},{
					xtype: 'textfield',
					id: 'ca_description',
					name: 'ca_description',
					flex:0.32,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				}],
				getValue: function() {
					var a = Ext.getCmp('ca_code');
					if(!Ext.isEmpty(a.value)) {
						return {ca_code: a.value};
					}
					return null;
				}
			
			},{
				xtype: 'checkbox',
				id: 'chkzerobalance',
				name: 'chkzerobalance',
				columnWidth: .51,
				boxLabel: '余额为零的不显示'
			},{
				xtype: 'checkbox',
				id: 'chknoamount',
				name: 'chknoamount',
				columnWidth: .51,
				boxLabel: '无发生额的不显示'
			},{
				xtype: 'checkbox',
				id: 'chknoamandzbal',
				name: 'chknoamandzbal',
				columnWidth: .51,
				boxLabel: '余额为零且无发生额的不显示'
			},{
				xtype: 'checkbox',
				id: 'chkstatis',
				name: 'chkstatis',
				checked:true,
				columnWidth: .51,
				boxLabel: '是否显示汇总数'
			}],
			buttonAlign: 'center',
    	    buttons: [{
	    		text: '确定',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		handler: function(btn) {
    				var fl = btn.ownerCt.ownerCt;
					var	con = me.getCondition(fl);
					var grid = Ext.getCmp('arquerygrid');
					me.condition = con;
					me.query(con);
					fl.hide();
	    		}
	    	},{
	    		text: '关闭',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		handler: function(btn) {
	    			
	    			var fl = btn.ownerCt.ownerCt;
	    			fl.hide();
	    		}
	    	}]
    	});
		return filter;
    
    },
    getCondition: function(pl) {
    	var r = new Object(),v;
    	Ext.each(pl.items.items, function(item){
    		if(item.getValue !== undefined) {
    			v = item.getValue();
        		if(!Ext.isEmpty(v)) {
        			r[item.id] = v;
        		}
    		}
    	});
    	var tb = Ext.getCmp('gl_info_ym');
    	if(tb)
    		tb.updateInfo(r);
    	return r;
    },
    query: function(cond) {
    	var me = this;
    	cond = cond || me.getCondition(me.filter);
    	var grid = me.getGrid();
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/gs/ArQueryController/getArQuery.action',
    		params: {
    			condition: Ext.encode(cond)
    		},
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			//data
            		var data = [];
            		if(!res.data){
            			me.GridUtil.add10EmptyData(grid.detno, data);
            			me.GridUtil.add10EmptyData(grid.detno, data);//添加20条空白数据
            		} else {
            			if(res.data instanceof Array) {
            				data = res.data;
            			} else {
            				data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            			}
            		}
            		//view
            		if(grid.selModel.views == null){
            			grid.selModel.views = [];
            		}
            		grid.store.loadData(data);
            		var lockedView = grid.view.lockedView;
                    if(lockedView){
                        var tableEl = lockedView.el.child('.x-grid-table');
                        if(tableEl){
                      	  tableEl.dom.style.marginBottom = '9px';
                        }
                    }
        		}
        	}
    	});
    },
	getCurrentYearmonth: function(f) {
		Ext.Ajax.request({
			url: basePath + 'fa/getMonth.action',
			params: {
    			type: 'MONTH-B'
    		},
			method: 'GET',
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if(rs.data) {
					f.setValue(rs.data.PD_DETNO);
				}
			}
		});
	}
});