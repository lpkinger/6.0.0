Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.VmCopQuery', {
    extend: 'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
    condition:'',
    views:[
     		'fa.arp.vmCopQuery.Viewport','fa.arp.vmCopQuery.GridPanel','fa.arp.vmCopQuery.QueryForm','fa.arp.vmCopQuery.QueryWin',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.ConDateField','core.form.YnField',
     		'core.form.FtDateField','core.form.FtFindField','core.grid.YnColumn','core.grid.TfColumn','core.form.ConMonthDateField',
     		'core.button.Refresh'
     	],
    refs: [{ref: 'grid', selector: '#vmcopquerygrid'}],
    init:function(){
    	var me = this;
    	this.control({
    		'erpVmCopQueryFormPanel button[name=confirm]': {
    			
    		},
    		'button[name=refresh]':{
    			click: function(btn){
    				me.getGrid().setLoading(true);
    				Ext.Ajax.request({
    					url: basePath + 'fa/arp/VmQueryController/refreshVmCopQuery.action',
    					method: 'GET',
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
    		modal:true,
    		width: 500,
    		height: 385,
    	    layout: 'column',
    	    defaults: {
    	    	margin: '2 2 2 10'
    	    },
    	    items: [{
				id: 'vm_yearmonth',
				name: 'vm_yearmonth',
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
				id: 'vm_currency',
				name:'vm_currency',
				margin: '10 2 2 10',
				flex: 0.2,
				columnWidth: .51
				
			},{

				xtype: 'combo',
				fieldLabel: '公司',
				height: 23,
				labelWidth: 80,
				id: 'vm_cop',
				name:'vm_cop',
				margin: '10 2 2 10',
				flex: 0.2,
				columnWidth: .51,
				editable:false,
				queryMode : 'local',
				displayField:'display',
				valueField : 'value',
				store:new Ext.data.Store({
					fields:['value','display'],
					data:[
					      {'value':'黑白屏事业部','display':'黑白屏事业部'},
					      {'value':'彩屏事业部','display':'彩屏事业部'},
					      {'value':'触控事业部','display':'触控事业部'}
					      ]
				})

				
			},{

				fieldLabel: '供应商编码',
				labelWidth: 80,
				height: 23,
				layout: 'hbox',
				columnWidth: 1,
				xtype: 'fieldcontainer',
				id: 'vmq_vendcode',
				defaults: {
					fieldStyle : "background:#FFFAFA;color:#515151;"
				},
				items: [{
					labelWidth: 35,
					xtype: 'dbfindtrigger',
					flex: 0.32,
					id: 'vm_vendcode',
					name: 'vm_vendcode'
				},{
					xtype: 'textfield',
					id: 'vm_vendname',
					name: 'vm_vendname',
					flex:0.32,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				}],
				getValue: function() {
					var a = Ext.getCmp('vm_vendcode');
					if(!Ext.isEmpty(a.value)) {
						return {vm_vendcode: a.value};
					}
					return null;
				}
			
			},{
				xtype: 'checkbox',
				id: 'chkumio',
				name: 'chkumio',
				columnWidth: .51,
				boxLabel: '包含未开票未转应付暂估出货'
			
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
				id: 'chkstatis',
				name: 'chkstatis',
				checked:true,
				columnWidth: .51,
				boxLabel: '是否显示汇总数'
			}
			
			],
			buttonAlign: 'center',
    	    buttons: [{
	    		text: '确定',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		handler: function(btn) {
    				var fl = btn.ownerCt.ownerCt;
					var	con = me.getCondition(fl);
					var grid = Ext.getCmp('vmcopquerygrid');
					grid.chkumio = Ext.getCmp('chkumio').getValue();
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
	getCurrentYearmonth: function(f) {
		Ext.Ajax.request({
			url: basePath + 'fa/arp/getCurrentYearmonth.action',
			method: 'GET',
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if(rs.data) {
					f.setValue(rs.data);
				}
			}
		});
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
    		url: basePath + 'fa/arp/VmQueryController/getVmCopQuery.action',
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
            		if(!res.data || res.data.length == 2){
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
    }
});