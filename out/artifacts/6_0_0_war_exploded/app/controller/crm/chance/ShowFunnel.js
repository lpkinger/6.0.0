Ext.QuickTips.init();
Ext.define('erp.controller.crm.chance.ShowFunnel', {
    extend: 'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
     		'crm.chance.ShowFunnel.Viewport','crm.chance.ShowFunnel.GridPanel','core.trigger.DbfindTrigger',
     		'core.form.FtField','core.form.ConDateField','core.form.YnField','core.form.FtDateField',
     		'core.form.FtFindField','core.grid.YnColumn','core.grid.TfColumn','core.form.ConMonthDateField'
     	],
    init:function(){
    	this.control({
    		'erpQueryFormPanel button[name=confirm]': {
    			
    		},
    		/*'button[name=hide]':{
    			afterrender: function(btn){
    				var me = this;
    				me.funnel();
    			}
    		},*/
    		'button[name=query]':{

    			afterrender: function(btn){
    				controller=this;
    				var me = this;
    				var filter = me.createFilterPanel(btn);
    				filter.show();
    				me.funnel();
    			},
    			click: function(btn){
    				var me = this;
    		    	if(Ext.getCmp(btn.getId() + '-filter')){
    		    		Ext.getCmp(btn.getId() + '-filter').show();
    		    	}else{
    		    		var filter = me.createFilterPanel(btn);
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
    	    items: [/*{
				id: 'cm_yearmonth',
				name: 'cm_yearmonth',
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
			},*/{

				xtype: 'dbfindtrigger',
				fieldLabel: '任务执行人',
				height: 23,
				labelWidth: 80,
				id: 'ch_tasker',
				name:'ch_tasker',
				margin: '10 2 2 10',
				flex: 0.2,
				columnWidth: .51
				
			},{

				fieldLabel: '客户编码',
				labelWidth: 80,
				height: 23,
				layout: 'hbox',
				columnWidth: 1,
				xtype: 'fieldcontainer',
				id: 'chq_cucode',
				defaults: {
					fieldStyle : "background:#FFFAFA;color:#515151;"
				},
				items: [{
					labelWidth: 35,
					xtype: 'dbfindtrigger',
					flex: 0.32,
					id: 'ch_cucode',
					name: 'ch_cucode'
				},{
					xtype: 'textfield',
					id: 'ch_cuname',
					name: 'ch_cuname',
					flex:0.32,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				}],
				getValue: function() {
					var a = Ext.getCmp('ch_cucode');
					if(!Ext.isEmpty(a.value)) {
						return {ch_cucode: a.value};
					}
					return null;
				}
			
			}/*,{

				xtype: 'checkbox',
				id: 'chkumio',
				name: 'chkumio',
				columnWidth: .51,
				boxLabel: '包含未开票未转发出商品出货'
			
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
			}*/],
			buttonAlign: 'center',
    	    buttons: [{
	    		text: '确定',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		handler: function(btn) {
    				var fl = btn.ownerCt.ownerCt;
					var	con = me.getCondition(fl);
					f=fl;
					var grid = Ext.getCmp('crmquerygrid');
					//grid.chkumio = Ext.getCmp('chkumio').getValue();
					me.query(con);
					fl.hide();
	    		}
	    	},{
	    		text: '关闭',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		name:'hide',
	    		handler: function(btn) {
	    			
	    			var fl = btn.ownerCt.ownerCt;
	    			fl.hide();
	    		}
	    	}]
    	});
		return filter;
    
    },
//	getCurrentYearmonth: function(f) {
//		Ext.Ajax.request({
//			url: basePath + 'fa/arp/getCurrentYearmonth.action',
//			method: 'GET',
//			callback: function(opt, s, r) {
//				var rs = Ext.decode(r.responseText);
//				if(rs.exceptionInfo) {
//					showError(rs.exceptionInfo);
//				} else if(rs.data) {
//					f.setValue(rs.data);
//				}
//			}
//		});
//	},
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
    funnel:function(){
		var funneldata=new Array();
		Ext.Ajax.request({
			url:basePath+'crm/funnel.action',
			params:{
				condition: {}
			},
			method : 'post',
			async:false,
			callback : function(options,success,response){
				sum=0;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
				if(res.data){
					var i=0;
					var data=res.data;
					Ext.each(data,function(da){
						Ext.each(Ext.Object.getKeys(da),function(key){
							sum+=da[key];
							var d=new Array();
							d[0]=key,d[1]=da[key];
							funneldata[i]=d;
							i++;
						});
					});
				}
			}
		});
		chancefunnel=new Highcharts.Chart({
			 chart: {
				 reflow: false,
				 renderTo: 'panel-body',
		            type: 'funnel',
		            marginRight: 100,
		            width:800,
		            heigth:298
		        },
		        title: {
		            text: '商机销售漏斗',
		            x: -50
		        },
		        plotOptions: {
		            series: {
		                dataLabels: {
		                    enabled: true,
		                    formatter:function(){//<a href="javascript:void(0)" onclick="controller.clickfunnel(this)"></a>
		                    	return '<b>'+this.key+'</b>  ('+this.y +')--'+Math.round(this.y/sum*100)+'% ';
		                    },
		                    color: 'black',
		                    softConnector: true
		                },
		                neckWidth: '30%',
		                neckHeight: '25%',
		                events:{
		                	 click:function(e){
		                		 controller.clickfunnel(e.point.name);
				               }
		                }
		            }
		        },
		        legend: {
		            enabled: false
		        },
		        series: [{
		            name: '数  量',
		            data:funneldata
		        }]
		});
    },
    query: function(cond) {
    	var me = this;
    	var grid = Ext.getCmp('crmquerygrid');
    	grid.setLoading(true);
		var funneldata=new Array();
		Ext.Ajax.request({
			url:basePath+'crm/funnel.action',
			params:{
				condition: Ext.encode(cond)
			},
			method : 'post',
			async:false,
			callback : function(options,success,response){
				sum=0;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
				if(res.data){
					var i=0;
					var data=res.data;
					Ext.each(data,function(da){
						Ext.each(Ext.Object.getKeys(da),function(key){
							sum+=da[key];
							var d=new Array();
							d[0]=key,d[1]=da[key];
							funneldata[i]=d;
							i++;
						});
					});
				}
			}
		});
		chancefunnel.series[0].setData(funneldata);
    	Ext.Ajax.request({
    		url: basePath + 'crm/chance/getChanceQuery.action',
    		params: {
    			condition: Ext.encode(cond)
    		},
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		me.doQuery(grid,res);
        	}
    	});
    },
    clickfunnel: function(con) {
    	
    	var me = this;
    	var cond=me.getCondition(f);
    	if(con){
    		cond['ch_stage']=con;
    	}
    	var grid = Ext.getCmp('crmquerygrid');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'crm/chance/getChanceQuery.action',
    		params: {
    			condition: Ext.encode(cond)
    		},
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		me.doQuery(grid,res);
        	}
    	});
    },
    doQuery:function(grid,res){
    	var me=this;
    	if(res.exceptionInfo){
			showError(res.exceptionInfo);return;
		}
		if(res.columns){
			var limits = res.limits, limitArr = new Array();
			if(limits != null && limits.length > 0) {//权限外字段
				limitArr = Ext.Array.pluck(limits, 'lf_field');
			}
			Ext.each(res.columns, function(column, y){
				//power
				if(limitArr.length > 0 && Ext.Array.contains(limitArr, column.dataIndex)) {
					column.hidden = true;
				}
				//renderer
				me.GridUtil.setRenderer(grid, column);
				//logictype
				me.GridUtil.setLogicType(grid, column);
			});
			//data
    		var data = [];
    		if(!res.data || res.data.length == 2){
    			me.GridUtil.add10EmptyData(grid.detno, data);
    			me.GridUtil.add10EmptyData(grid.detno, data);//添加20条空白数据
    		} else {
    			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
    		}
    		if(grid.columns && grid.columns.length > 0) {
    			grid.store.loadData(data);return;
    		}
    		//store
    		var store = me.GridUtil.setStore(grid, res.fields, data, grid.groupField, grid.necessaryField);
    		//view
    		if(grid.selModel.views == null){
    			grid.selModel.views = [];
    		}
    		//dbfind
    		if(res.dbfinds.length > 0){
    			grid.dbfinds = res.dbfinds;
    		}
			//toolbar
    		me.GridUtil.setToolbar(grid, res.columns, res.necessaryField);
    		//reconfigure
    		grid.reconfigure(store, res.columns);
		}
    }
});