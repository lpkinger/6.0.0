Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.BusinessCondition', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.cust.BusinessCondition', 'core.grid.Panel2','core.toolbar.Toolbar',
			'core.button.Save', 'core.button.Upload','core.button.Close','core.button.Delete',
			'core.button.Update','core.button.Export',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn',
			'core.form.StatusField','core.form.FileField','core.form.MultiField'],
	init : function() {
		var me = this;
		this.control({
			'field[name=bc_id]': {
				afterrender:function(field){
					if(formCondition){
						var id = formCondition.substring(formCondition.indexOf('=')+1);
						field.setValue(id);
					}
				}
			},
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
        	'field[name=bc_bcremark]': {
    			beforerender : function(f) {
    				f.emptyText = '描述实际控制人及公司经营思路、经营管理团队情况（技术、研发、生产、销售）、生产管理（含专利技术等）、非生产管理、销售情况等';
				}
    		},
    		'field[name=bc_upremark]': {
    			beforerender : function(f) {
    				f.emptyText = '重点对本公司在采购渠道、采购成本方面是否有优势加以简要说明';
				}
    		},
    		'field[name=bc_downremark]': {
    			beforerender : function(f) {
    				f.emptyText = '重点对本公司在销售方面是否具有优势，若授信是用于拟增加产量、销售额的，需说明销售渠道如何开拓，拟据展的重点下游客户等作说明';
				}
    		},
    		'field[name=bc_industryremark]': {
    			beforerender : function(f) {
    				f.emptyText = '包括近期主营产品价格变动及行业盈利能力变化情况，近一年内的行业重大影响事件，本公司在细分行业、区域市场的地位等';
				}
    		},
    		'field[name=bc_sweaterprocess]': {
    			beforerender : function(f) {
    				f.emptyText = '分别对供应商及客户的贸易流程（采购、生产、销售、物流、对账、回款）进行详细描述，具体如何采购、生产、销售，物流途径是什么，如何对账，付款方式如何，有无账期、账期多久，回款通过什么途径回等';
				}
    		},
    		'erpSaveButton': {
    			afterrender:function(btn){
					if(readOnly==1){
						btn.hide();
					}
				},
    			click: function(btn){
    				me.beforeSave();	
    			}
        	}
		})
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	beforeSave:function(isUpdate){
		var me = this;
		var form = Ext.getCmp('form');
		
		var grid1 = Ext.getCmp('BC_ProductMix');
		var grid2 = Ext.getCmp('BC_UpstreamCust');	
		var grid3 = Ext.getCmp('BC_DownstreamCust');
		var grid4 = Ext.getCmp('BC_ProposedFinance');
		var grid5 = Ext.getCmp('BC_YearDeal');
		
		var ratios = 0;
		var ratio = 0;
		var data2 = me.getAllGridStore(grid2);
		if(data2.length > 5){
			showError("上游客户只能填写5个！");
			return;
		}
		var data3 = me.getAllGridStore(grid3);
		if(data2.length > 5){
			showError("下游客户只能填写5个！");
			return;
		}
		var param1 = new Array();
		if(grid1){
			param1 = me.GridUtil.getGridStore(grid1);
		}
		var param2 = new Array();
		if(grid2){
			param2 = me.GridUtil.getGridStore(grid2);
		}
		var param3 = new Array();
		if(grid3){
			param3 = me.GridUtil.getGridStore(grid3);
		}
		var param4 = new Array();
		if(grid4){
			param4 = me.GridUtil.getGridStore(grid4);
		}
		var param5 = new Array();
		if(grid5){
			param5 = me.GridUtil.getGridStore(grid5);
		}
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
		param4 = param4 == null ? [] : "[" + param4.toString().replace(/\\/g,"%") + "]";
		param5 = param5 == null ? [] : "[" + param5.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
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
			me.save(r, param1, param2, param3, param4, param5, isUpdate);
		} else{
			me.FormUtil.checkForm();
		}		
	},
	save: function(){
		var me = this;
		var form = Ext.getCmp('form');
		var params = new Object();
		var r = arguments[0],isUpdate = arguments[arguments.length-1];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});	
		params.caller = caller;
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param1 = unescape(arguments[1].toString().replace(/\\/g,"%"));
		params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
		params.param3 = unescape(arguments[3].toString().replace(/\\/g,"%"));
		params.param4 = unescape(arguments[4].toString().replace(/\\/g,"%"));
		params.param5 = unescape(arguments[5].toString().replace(/\\/g,"%"));
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
	   		url : basePath + 'fs/cust/saveBusinessCondition.action?_noc=1',
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){	
	   			me.FormUtil.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				showMessage('提示', '保存成功!', 1000);
    				window.location.reload();
	   			} else if(localJson.exceptionInfo){
   					showError(localJson.exceptionInfo);
	   				return;
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		}
		});
	},
	getAllGridStore: function(grid){
		var me = this,GridData = new Array();
		var form = Ext.getCmp('form');
		if(grid != null){
			var s = grid.getStore().data.items;//获取store里面的数据
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				if(!me.GridUtil.isBlank(grid, data)){
					if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
						data[grid.mainField] = Ext.getCmp(form.keyField).value;
					}
					Ext.each(grid.columns, function(c){
						if((!c.isCheckerHd)&&(c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
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
					GridData.push(dd);
				}
			}
		}
		return GridData;
	}
});