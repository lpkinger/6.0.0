Ext.define('erp.view.sys.alert.AlertInsViewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				id: 'form',
				anchor: '100% 68%',
				saveUrl: 'sys/alert/saveAlertInstance.action',
				deleteUrl: 'sys/alert/deleteAlertInstance.action',
				updateUrl: 'sys/alert/updateAlertInstance.action',
				auditUrl: 'sys/alert/auditAlertInstance.action',
				resAuditUrl: 'sys/alert/resAuditAlertInstance.action',
				submitUrl: 'sys/alert/submitAlertInstance.action',
				resSubmitUrl: 'sys/alert/resSubmitAlertInstance.action',
                bannedUrl:'sys/alert/bannedAlertInstance.action',
                resBannedUrl:'sys/alert/resBannedAlertInstance.action',
				getIdUrl: 'common/getId.action?seq=ALERT_ITEM_INSTANCE_SEQ',
				/*printUrl:'scm/sale/printSaleForecast.action',*/
				itemField: 'aii_itemid',
				keyField: 'aii_id',
				codeField: 'aii_code',
				statusField: 'aii_status',
				trackResetOnLoad: true,
				getItemsAndButtons: function(form, url, param){
					var FormUtil = Ext.create('erp.util.FormUtil'),
						tab = FormUtil.getActiveTab();
					FormUtil.setLoading(true);
					Ext.Ajax.request({//拿到form的items
						url : basePath + url,
						params: param,
						method : 'post',
						callback : function(options, success, response){
							FormUtil.setLoading(false);
							if (!response) return;
							var res = new Ext.decode(response.responseText);
							if(res.exceptionInfo != null){
								showError(res.exceptionInfo);return;
							}
							form.fo_id = res.fo_id;
							form.fo_keyField = res.keyField;
							form.tablename = res.tablename;//表名
							if(res.keyField){//主键
								form.keyField = res.keyField;
							}
							if(res.statusField){//状态
								form.statusField = res.statusField;
							}
							if(res.statuscodeField){//状态码
								form.statuscodeField = res.statuscodeField;
							}
							if(res.codeField){//Code
								form.codeField = res.codeField;
							}
							if(res.dealUrl){
								form.dealUrl = res.dealUrl;
							}
							if(res.mainpercent && res.detailpercent){
								form.mainpercent = res.mainpercent;
								form.detailpercent = res.detailpercent;
							}
							form.fo_detailMainKeyField = res.fo_detailMainKeyField;//从表外键字段
							//data&items
							// 添加参数form
							res.items.push({
								xtype: 'paramform',
								id: 'paramForm',
								border: false,
								autoScroll: true,
								padding: 0,
								bodyPadding: 0,
								layout: 'column',
								cls: 'u-form-default',
								columnWidth: 1,
								group: 2 // 放入第二个分组中以实现收缩展开
							});
							var items = FormUtil.setItems(form, res.items, res.data, res.limits, {
								labelColor: res.necessaryFieldColor
							});
							form.add(items);
							if(!form._nobutton) FormUtil.setButtons(form, res.buttons);
							//form第一个可编辑框自动focus
							FormUtil.focusFirst(form);
							form.fireEvent('afterload', form);
						}
					});
				},
				/**
			   	 * 只保留属于基础设置的itemValue
			   	 */
			   	getBaseValues: function() { 
			   		var paramForm = Ext.getCmp('paramForm'),
			   			allValues = this.getForm().getValues(),
			   			baseValues = Object.assign({},allValues);
			   			paramValues = paramForm?paramForm.getForm().getValues():{};
			   		for(var key in paramValues){
					    delete baseValues[key];
					}
			   		
			   		return baseValues;
			   	}
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 32%',
				detno: 'aia_detno',
				keyField: 'aia_id',
				mainField: 'aia_aiiid',
				allowExtraButtons: true
			}]
		}); 
		me.callParent(arguments); 
	} 
});