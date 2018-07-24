Ext.QuickTips.init();
Ext.define('erp.controller.common.VisitERP.BomTemplate', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
    		'common.VisitERP.BomTemplate','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.button.BomCopy',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail','core.button.CallProcedureByConfig',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.grid.YnColumn','core.button.Flow','core.button.Print',
    		'core.button.SonBOM','core.button.Replace','core.button.FeatureDefinition','core.button.PrintByCondition',
    		'core.button.Banned','core.button.ResBanned','core.form.FileField','core.button.Sync','core.button.FeatureQuery',
    		'core.button.LoadRelation','core.button.Modify',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.MultiField','core.button.BOMTurn','core.button.BomUpdatePast'
    	],
    init:function(){
    	var me = this;
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	this.control({ 
    		'erpCloseButton': {
    			afterrender: function(){
    				var s = Ext.getCmp('form').dockedItems.items[0];
    				var bomId = Ext.getCmp('bo_id').value;
    				//var cu_uu = 10043574;
    				var cu_uu = Ext.getCmp('cu_uu').value;
    				s.insert(1,{
    					cls:'x-btn-gray',
    					iconCls:'x-button-icon-query',
    					xtype: 'button',
    					height: 26,
    					text: '客户物料资料对照',
    					handler: function(btn){
    						Ext.create('Ext.window.Window', {
    							title: '客户物料资料对照',
    							id: 'cpwin',
    						    height: '80%',
    						    width: '90%',
    						    autoScroll: true,
    						    bbar: ['->',{
    						    	xtype: 'button',
    						    	cls: 'x-btn-gray',
    						    	iconCls: 'x-button-icon-submit',
    						    	text: '转新物料申请',
    						    	handler: function(btn){
    						    		var grid = Ext.getCmp('dataGrid');
    						    		var temp = grid.selModel.selected.items;
    						    		if(temp.length > 0){
    						    			var data = new Array();
    						    			Ext.Array.each(temp, function(item){
    						    				data.push(me.trunData(item.data));
    						    			});
    						    			console.log(data);
    						    			Ext.Ajax.request({
    						    				url: basePath + 'common/VisitERP/trunPreproduct.action',
    						    				params: {
    						    					data: JSON.stringify(data)
    						    				},
    						    				success: function(response){
    						    					var res = Ext.decode(response.responseText);
    						    					if(res.success){
    						    						Ext.Msg.alert('提示',res.message);
    						    					}
    						    				}
    						    			});
    						    		}else{
    						    			Ext.Msg.alert('提示','请至少选择一条记录!');
    						    		}
    						    	}
    						    },{
						        	xtype: 'button',
						        	cls:'x-btn-gray',
						        	iconCls:'x-button-icon-save',
						        	text:'保存',
						        	handler: function(btn){
						        		var grid = Ext.getCmp('dataGrid');
						        		var param = me.GridUtil.getGridStore(grid);
						        		//传入到后台保存
						        		Ext.Ajax.request({
						        			url: basePath + 'common/VisitERP/saveGridStore.action',
						        			params: {
						        				data: param
						        			},
						        			success: function(response){
						        				var res = Ext.decode(response.responseText);
						        				if(res.success){
						        					Ext.Msg.alert('提示', '保存成功', function(btn,text){
						        						if(btn == 'ok'){
						        							location.reload();
						        						}
						        					});
						        				}else{
						        					Ext.Msg.alert('提示', '保存失败,违反唯一约束条件(客户编号、物料编号)');
						        				}
						        					
						        			}
						        		});
						        	}
						        },{
						        	xtype: 'button',
						        	cls:'x-btn-gray',
						        	iconCls:'x-button-icon-close',
						        	text:'关闭',
						        	handler: function(btn){
						        		Ext.getCmp('cpwin').close();
						        	}
						        },'->'],
    						    //layout: 'fit',
    						    items: {
    						    	layout: 'fit',
    						    	height: '100%',
        						    width: '100%',
    						        xtype: 'grid',
    						        selModel:Ext.create('Ext.selection.CheckboxModel',{mode:"SIMPLE"}),
    						        /*bbar: {xtype: 'erpToolbar',id:'toolbar'},*/
    						        autoScroll: true,
    						        id: 'dataGrid',
    						        listeners: {
    						        	'selectionchange': function(row, record, index, eOpts ){
    						        		Ext.Array.each(record, function(item){
    						        			if(item.get('pc_prodcode')){
        						        			row.deselect(item);
        						        		}
    						        		});
    						        	}
    						        },
    						        dbfinds: [{
    						        	dbGridField:"pr_code",
    						        	field:"pc_prodcode"
    						        },{
    						        	dbGridField:"pr_detail",
    						        	field:"pc_proddetail"
    						        },{
    						        	dbGridField:"pr_spec",
    						        	field:"pc_prodspec"
    						        }],
    						        plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
    						            clicksToEdit: 1
    						        }), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
    						        border: false,
    						        columns: [{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_detno",
    						        	header:"序号",
    						        	width:40
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_id",
    						        	header:"ID",
    						        	width:0
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_custcode",
    						        	header:"客户编号",
    						        	width:100
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_custname",
    						        	header:"客户名称",
    						        	width:160
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_custprodcode",
    						        	header:"客户料号",
    						        	width:140
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_custproddetail",
    						        	header:"客户物料名称",
    						        	width:140
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_custprodspec",
    						        	header:"客户物料规格",
    						        	width:120
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_custprodorispeccode",
    						        	header:"客户物料原厂型号",
    						        	width:100
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_custprodbrand",
    						        	header:"客户物料品牌",
    						        	width:100
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_custprodkind",
    						        	header:"客户物料大类",
    						        	width:80
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_custprodkind2",
    						        	header:"客户物料中类",
    						        	width:80
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_custprodkind3",
    						        	header:"客户物料小类",
    						        	width:80
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_custprodzxbzs",
    						        	header:"客户物料最小包装量",
    						        	width:80
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_prodcode",
    						        	dbfind:"BomTemplate|pr_code",
    						        	editable: true,
    						        	editor:{
    						        		allowDecimals:true,
    						        		displayField:"display",
    						        		editable:true,
    						        		hideTrigger:false,
    						        		queryMode:"local",
    						        		valueField:"value",
    						        		xtype:"dbfindtrigger"
    						        	},
    						        	header:"物料编号",
    						        	width:120
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_proddetail",
    						        	logic:"ignore",
    						        	header:"物料名称",
    						        	width:140
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_prodspec",
    						        	header:"物料规格",
    						        	logic:"ignore",
    						        	width:140
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pc_custid",
    						        	header:"客户ID",
    						        	hidden: true
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pr_manutype",
    						        	header:"生产类型",
    						        	hidden: true,
    						        	logic:"ignore"
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pr_supplytype",
    						        	header:"供应类型",
    						        	hidden: true,
    						        	logic:"ignore"
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pr_dhzc",
    						        	header:"计划类型",
    						        	hidden: true,
    						        	logic:"ignore"
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pr_jitype",
    						        	header:"适用机型",
    						        	hidden: true,
    						        	logic:"ignore"
    						        },{
    						        	align:"left",
    						        	cls:"x-grid-header-1",
    						        	dataIndex:"pr_unit",
    						        	header:"单位",
    						        	hidden: true,
    						        	logic:"ignore"
    						        }],               
    						        store:Ext.create('Ext.data.Store',{
    								    storeId:'viewstore',
    									fields : [ 'pc_detno','pc_custcode','pc_custname','pc_custprodcode','pc_custproddetail','pc_custprodspec','pc_custprodorispeccode',
    									           'pc_custprodbrand','pc_custprodkind','pc_custprodkind2','pc_custprodkind3','pc_custprodzxbzs','pc_prodcode','pc_proddetail',
    									           'pc_prodspec','pc_custid','pr_manutype','pr_supplytype','pr_dhzc','pr_jitype','pr_unit'],
    								    proxy: {
    								        type: 'ajax',
    								        url: basePath + 'common/VisitERP/getGridStore.action',
    								        extraParams:{
    											caller : caller,
    											bomid : bomId,
    											cu_uu: cu_uu
    								        },
    								        reader: {
    								            type: 'json',
    								            root: 'data'
    								        }
    								    },
    								    autoLoad: true
    								}),
    						    }
    							
    						}).show();
    					}
    				});
    				s.insert(2,{
    					xtype: 'button',
    					text: '执行',
    					cls:'x-btn-gray',
    					height: 26,
    					iconCls:'x-button-icon-check',
    					listeners: {
    						afterrender: function(btn){
    							var val = Ext.getCmp('bo_invalidstatus').value
    							if(cu_uu == ''){
    								btn.hide();
    							}else if(val == '已作废'){
    								btn.setDisabled(true);
    							}else{
    								Ext.Ajax.request({
    									url: basePath + 'common/VisitERP/validConvertTurn.action',
    									params: {
    										bomid: bomId,
    										cu_uu: cu_uu
    									},
    									async: false,
    									success: function(response){
    										var res = Ext.decode(response.responseText);
    										if(!res.success)
    											btn.hide();
    									}
    								});
    							}
    						},
    						click: function(btn){
    							var formStore = me.getFormStore();
    							var param = me.getGridStore();
    							Ext.Ajax.request({
    								url: basePath + 'common/VisitERP/TrunFormal.action',
    								params:{
    									formStore: formStore,
    									gridStore: param,
    									bomId: bomId
    								},
    								success: function(response){
    									var res = Ext.decode(response.responseText);
    									if(res.success){
    										showMessage('提示',res.message);
    									}else{
    										Ext.Msg.alert('提示',res.message);
    									}
    								}
    							});
    						}
    					}
    				});
    			}
    		},
    		'erpExportDetailButton': {
    			afterrender: function(){
    				
    			}
    		}
    	});
    },
    getFormStore: function(){
    	var form = Ext.getCmp('form');
    	if(form.getForm().isValid()){
			//form里面数据
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					//number类型赋默认值，不然sql无法执行
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'itemgrid'){
					//number类型赋默认值，不然sql无法执行
					if(item.value != null && item.value != ''){
						r[item.name]=item.value;
					}
				}
			});

			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
				
			});
			
			Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
				if(contains(k, '-', true) && !contains(k,'-new',true)){
					delete r[k];
				}
			});
			return unescape(escape(Ext.JSON.encode(r)));
		}
    },
    getGridStore: function(){
    	var grid = Ext.getCmp('grid');
    	var me = this,
			jsonGridData = new Array();
    	var form = Ext.getCmp('form');
    	if(grid!=null){
    		var s = grid.getStore().data.items;//获取store里面的数据
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				if(!me.isBlank(grid, data)){
					Ext.each(grid.columns, function(c){
						if((!c.isCheckerHd)&&(c.logic != 'ignore') && c.dataIndex){
							if(c.xtype == 'datecolumn'){
								c.format = c.format || 'Y-m-d';
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
									}else  dd[c.dataIndex]=null;
								}
							} else if(c.xtype == 'datetimecolumn'){
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
									}
								}
							} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
									dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
								} else {
									dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
								}
							} else {
								dd[c.dataIndex] = s[i].data[c.dataIndex];
							}
							if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
								dd[c.dataIndex] = c.defaultValue;
							}
						}
					});
					if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
						dd[grid.mainField] = Ext.getCmp(form.keyField).value;
					}
					jsonGridData.push(Ext.JSON.encode(dd));
				}
			}
    	}
    	return jsonGridData;
    },
    isBlank: function(grid, data) {
		if(typeof grid.isEmptyRecord === 'function') {
			return grid.isEmptyRecord.call(grid, data);
		} else {
			var ff = grid.necessaryFields,bool = true;
			var of = grid.orNecessField, c;
			if(ff) {
				bool = false;
				Ext.each(ff, function(f) {
					c = grid.down('gridcolumn[dataIndex=' + f + ']');
					if(Ext.isEmpty(data[f]) || (data[f] === 0 && c 
							&& c.xtype != 'ynnvcolumn' && c.xtype != 'yncolumn' && !c.useNull )) {//数字空
						bool = true;return;
					}
				});
			} else if(of){
				Ext.each(of,function(f){
					if(!Ext.isEmpty(data[f]) && data[f] != 0){
						bool = false;
						return;
					}
				});
			} else {
				if(!grid.necessaryField || !Ext.isEmpty(data[grid.necessaryField])) {
					bool = false;
				} 
			}
			return bool;
		}
	},
	/**
	 * 将数据转成新物料申请需要的数据
	 */
    trunData: function(data){
    	var obj = new Object();
    	obj.pre_code 		= 	data.pc_custprodcode;			//物料编号
    	obj.pre_detail 		= 	data.pc_custproddetail;			//物料名称
    	obj.pre_spec		= 	data.pc_custprodspec;			//物料规格
    	obj.pre_unit		=	data.pr_unit;					//单位
    	obj.pre_manutype	=	data.pr_manutype;				//生产类型
    	obj.pre_supplytype	=	data.pr_supplytype;				//供应类型
    	obj.pre_dhzc		=	data.pr_dhzc;					//计划类型
    	obj.pre_machine		=	data.pr_jitype;					//使用机型
    	obj.pre_brand		= 	data.pc_custprodbrand;			//品牌
    	obj.pre_orispeccode	=	data.pc_custprodorispeccode;	//原厂型号
    	obj.pre_zxbzs		=	data.pc_custprodzxbzs;			//最小包装量
    	return obj;
    }
	
});