Ext.define('erp.view.common.Batchlevel.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpBatchDealFormPanel',
	id: 'dealform', 
    region: 'north',
    frame : true,
    header: false,//不显示title
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	tbar: [{
		name: 'query',
		id: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	handler: function(){
			Ext.getCmp('dealform').onQuery();
    	}
	}, '->',  {
    	xtype: 'erpMakeOccurButton',
    	id: 'erpMakeOccurButton',
    	hidden: true
    },
    {
    	xtype: 'erpSaleOccurButton',
    	id: 'erpSaleOccurButton',
    	hidden: true
    },
    {
    	xtype: 'erpVastAnalyseButton',
    	id: 'erpVastAnalyseButton',
    	hidden: true
    },{
    	xtype: 'erpVastDealButton',
    	id: 'erpVastDealButton',
    	hidden: true
    },{
	    	text : '确  认',
	    	iconCls: 'x-button-icon-save',
	    	cls: 'x-btn-gray',
	    	hidden: caller == 'Updatebomlevel!check'?false:true,
	    	handler : function(btn){
	    		var contentwindow = parent.Ext.getCmp('dbwin').body.dom.getElementsByTagName('iframe')[0].contentWindow;
	    		var grid = contentwindow.Ext.getCmp('batchDealGridPanel');
	    		var data = grid.getMultiSelected();
	    		if(data==''){
	    			showError("未勾选明细行数据，请选择明细行数据后确认!");
	    			return;
	    		}
	    		var singledata;
	    		var detailgrid = parent.Ext.getCmp('grid');
	    		var count = detailgrid.store.data.items.length;
	    		var length = count;
	    		var m=0;
	    		for(i=0;i<data.length;i++){
	    			dataLength = detailgrid.store.data.length;
	    			detailgrid.store.insert(dataLength+1,{});
	    			detailgrid.store.data.items[dataLength].set(detailgrid.columns[0].dataIndex,dataLength+1);//明细行自动编号
	    			singledata = data[i].data;
	    			if(i==0){
	    				for(j=0;j<dataLength+1;j++){
		    				if(detailgrid.store.data.items[j].data.ud_prodcode ==''||detailgrid.store.data.items[j].data.ud_prodcode ==null){
	    			    	    detailgrid.store.data.items[j].set('ud_prodcode',singledata.bs_soncode);
				    			detailgrid.store.data.items[j].set('ud_prodname',singledata.bs_sonname);
				    			detailgrid.store.data.items[j].set('ud_prodpesc',singledata.pr_spec);
				    			detailgrid.store.data.items[j].set('ud_produnit',singledata.pr_unit);
				    			detailgrid.store.data.items[j].set('ud_orilevel',singledata.bo_level);
				    			detailgrid.store.data.items[j].set('ud_bomid',singledata.bs_sonbomid);
   				    			m=1;
   				    			break;
		    				}
		    			}
	    			}
	    			if(m==1){
	    				detailgrid.store.data.items[j].set('ud_prodcode',singledata.bs_soncode);
		    			detailgrid.store.data.items[j].set('ud_prodname',singledata.bs_sonname);
		    			detailgrid.store.data.items[j].set('ud_prodpesc',singledata.pr_spec);
		    			detailgrid.store.data.items[j].set('ud_produnit',singledata.pr_unit);
		    			detailgrid.store.data.items[j].set('ud_orilevel',singledata.bo_level);
		    			detailgrid.store.data.items[j].set('ud_bomid',singledata.bs_sonbomid);
		    			j++;
	    			}
	    			if(m==0){
	    				detailgrid.store.data.items[length].set('ud_prodcode',singledata.bs_soncode);
		    			detailgrid.store.data.items[length].set('ud_prodname',singledata.bs_sonname);
		    			detailgrid.store.data.items[length].set('ud_prodpesc',singledata.pr_spec);
		    			detailgrid.store.data.items[length].set('ud_produnit',singledata.pr_unit);
		    			detailgrid.store.data.items[length].set('ud_orilevel',singledata.bo_level);
		    			detailgrid.store.data.items[length].set('ud_bomid',singledata.bs_sonbomid);
		    			length++;
	    			}
	    		}
	    		parent.Ext.getCmp('dbwin').close();
	    	}
    },
    '-',{
    	name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-excel',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var grid = Ext.getCmp('batchDealGridPanel');
    		grid.BaseUtil.exportexcel(grid);
    	}
    },'-',{
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		if(main){
    			main.getActiveTab().close();
    		}
    		parent.Ext.getCmp('dbwin').close();
    	}
	}],
	initComponent : function(){ 
    	this.getItemsAndButtons();
		this.callParent(arguments);
		this.addKeyBoardEvents();//监听Ctrl+Alt+S事件
	},
	getItemsAndButtons: function(){
		var me = this;
		me.FormUtil.getActiveTab().setLoading(true);
		Ext.Ajax.request({//拿到form的items
        	url : basePath + 'common/singleFormItems.action',
        	params: {
        		caller: caller, 
        		condition: ''
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.FormUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		me.fo_keyField = res.fo_keyField;
        		me.tablename = res.tablename;
        		if(res.keyField){
        			me.keyField = res.keyField;
        		}
        		if(res.dealUrl){
        			me.dealUrl = res.dealUrl;
        		}
        		me.fo_detailMainKeyField = res.fo_detailMainKeyField;
        		Ext.each(res.items, function(item){
        			if(screen.width < 1280){//根据屏幕宽度，调整列显示宽度
        				if(item.columnWidth > 0 && item.columnWidth <= 0.25){
        					item.columnWidth = 1/3;
        				} else if(item.columnWidth > 0.25 && item.columnWidth <= 0.5){
        					item.columnWidth = 2/3;
        				} else if(item.columnWidth >= 1){
        					item.columnWidth = 1;
        				}
        			} else {
        				if(item.columnWidth > 0.25 && item.columnWidth < 0.5){
        					item.columnWidth = 1/3;
        				}
        			}
        		});
        		me.add(res.items);
        		//解析buttons字符串，并拼成json格式
        		var buttonString = res.buttons;
        		if(buttonString != null && buttonString != ''){
        			if(contains(buttonString, '#', true)){
        				Ext.each(buttonString.split('#'), function(b, index){
        					if(!Ext.getCmp(b)){
        						var btn = Ext.getCmp('erpVastDealButton');
        						if(btn){
        							btn.ownerCt.insert(2, {
            							xtype: b
            						});
            						Ext.getCmp(b).show();
        						}
        					} else {
        						Ext.getCmp(b).show();
        					}
        				});
        			} else {
        				if(Ext.getCmp(buttonString)){
        					Ext.getCmp(buttonString).show();
        				} else {
        					var btn = Ext.getCmp('erpVastDealButton');//Ext.getCmp(buttonString);
                			if(btn){
                				btn.setText($I18N.common.button[buttonString]);
                				btn.show();
                			}
        				}
        			}
        		}
        	}
        });
	},
	beforeQuery: function(call, cond) {
		var str=null;
		Ext.Ajax.request({
			url: basePath + 'common/form/beforeQuery.action',
			params: {
				caller: call,
				condition: cond
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				}else if(rs.data){
					str=rs.data;
				}
			}
		});
		return str;
	},
	onQuery: function(){
		var grid = Ext.getCmp('batchDealGridPanel');
		grid.multiselected = new Array();
		var form = this;
		var bo_id = '';
		if(caller == 'Updatebomlevel!check'){
			bo_id = Ext.getCmp('bo_id').value;
			if(bo_id==''){
				showError('请选择BOMID!');
				return;
			}else{
				
			}
		}
		var condition = grid.defaultCondition || '';
		Ext.each(form.items.items, function(f){
			if(f.logic != null && f.logic != ''){
				if(f.xtype == 'checkbox' && f.value == true){
					if(condition == ''){
						condition += f.logic;
					} else {
						condition += ' AND ' + f.logic;
					}
				} else if(f.xtype == 'datefield' && f.value != null){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
					}
				} else if(f.xtype == 'datetimefield' && f.value != null){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d H:i:s');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					}
				} else if(f.xtype == 'numberfield' && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + '=' + f.value;
					} else {
						condition += ' AND ' + f.logic + '=' + f.value;
					}
				} else if(f.xtype == 'checkgroup' &&f.value !=null){
					if(contains(f.value, ',', true)){
					}else{
						condition += ' AND (' + f.logic + "='" + f.value + "')";
					}
				}else {
					//一般情况下，在执行批量处理时,是不需要把form的数据传回去,
					//但某些情况下，需要将form的某些字段的值也传回去
					//例如 请购批量转采购，如果指定了采购单号，就要把采购单号传回去
					if(contains(f.logic, 'to:', true)){
						if(!grid.toField){
							grid.toField = new Array();
						}
						grid.toField.push(f.logic.split(':')[1]);
					} else {
						if(f.value != null && f.value != ''){
							if(contains(f.value, 'BETWEEN', true) && contains(f.value, 'AND', true)){
								if(condition == ''){
									condition += f.logic + " " + f.value;
								} else {
									condition += ' AND (' + f.logic + " " + f.value + ")";
								}
							} else if(contains(f.value, '||', true)){
								var str = '';
								Ext.each(f.value.split('||'), function(v){
									if(v != null && v != ''){
										if(str == ''){
											str += f.logic + "='" + v + "'";
										} else {
											str += ' OR ' + f.logic + "='" + v + "'";
										}
									}
								});
								if(condition == ''){
									condition += str;
								} else {
									condition += ' AND (' + str + ")";
								}
							} else {
								if(condition == ''){
									condition += f.logic + "='" + f.value + "'";
								} else {
									condition += ' AND (' + f.logic + "='" + f.value + "')";
								}
							}
						}
					}
				}
			}
		});
		form.beforeQuery(caller,condition);
		var gridParam = {caller: caller, condition: condition};
		if(grid.getGridColumnsAndStore){
			grid.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
		} else {
			grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
		}

	},
	/**
	 * 监听一些事件
	 * <br>
	 * Ctrl+Alt+S	单据配置维护
	 * Ctrl+Alt+P	参数、逻辑配置维护
	 */
	addKeyBoardEvents: function(){
		var me = this;
		Ext.EventManager.addListener(document.body, 'keydown', function(e){
			if(e.altKey && e.ctrlKey) {
				if(e.keyCode == Ext.EventObject.S) {
					var url = "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + "&gridCondition=fd_foidIS" + me.fo_id, 
						forms = Ext.ComponentQuery.query('form'), 
						grids = Ext.ComponentQuery.query('gridpanel'),
						formSet = [], gridSet = [];
					if(forms.length > 0) {
						Ext.Array.each(forms, function(f){
							f.fo_id && (formSet.push(f.fo_id));
						});
					}
					if(grids.length > 0) {
						Ext.Array.each(grids, function(g){
							if(g.xtype.indexOf('erpBatchDealGridPanel') > -1)
								gridSet.push(window.caller);
							else if(g.caller)
								gridSet.push(g.caller);
						});
					}
					if(formSet.length > 0 || gridSet.length > 0) {
						url = "jsps/ma/multiform.jsp?formParam=" + formSet.join(',') + '&gridParam=' + gridSet.join(',');
					}
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', url);
				} else if(e.keyCode == Ext.EventObject.P) {
					me.FormUtil.onAdd('configs-' + caller, '逻辑配置维护(' + caller + ')', "jsps/ma/logic/config.jsp?whoami=" + caller);
				}
			}
		});
	}
});