/**
 * 更新供应商返还
 */	
Ext.define('erp.view.core.button.UpdateUseStatus',{
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateUseStatusButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	tooltip: '更新使用状况',
    	id: 'erpUpdateUseStatusButton',
    	text: $I18N.common.button.erpUpdateUseStatusButton,
    	width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners: {
			afterrender: function(btn) {
				var me = this;
				var status = Ext.getCmp('ac_statuscode');
				if(status && (status.value == 'ENTERING')){
					btn.hide();
				}
			}
		},
		handler: function() {
			var me = this, win = Ext.getCmp('Complaint-win');
			if(!win) {
				me.getComboData(function(data){
					var cs = Ext.getCmp('ac_usestatus'), val1 = cs ? cs.value : '';
					win = Ext.create('Ext.Window', {
						id: 'Complaint-win',
						title: '更新使用状况',
						height: 200,
						width: 400,
						items: [{
							xtype: 'form',
							height: '100%',
							width: '100%',
							bodyStyle: 'background:#f1f2f5;',
							items: [{
								margin: '10 0 0 0',
								xtype: 'combo',
								fieldLabel: '使用状况',
								name:'ac_usestatus',
								allowBlank: false,
								store: new Ext.data.Store({
									fields: ['display', 'value'],
									data : data
								}),
								queryMode: 'local',
								displayField: 'display',
								valueField: 'value',
								value: val1
							}],
							closeAction: 'hide',
							buttonAlign: 'center',
							layout: {
								type: 'vbox',
								align: 'center'
							},
							buttons: [{
								text: $I18N.common.button.erpConfirmButton,
								cls: 'x-btn-blue',
								handler: function(btn) {
									var form = btn.ownerCt.ownerCt,
										cs = form.down('textfield[name=ac_usestatus]');
									if(form.getForm().isDirty()) {
										me.updateUseStatus(Ext.getCmp('ac_id').value, cs.value);
									}
								}
							}, {
								text: $I18N.common.button.erpCloseButton,
								cls: 'x-btn-blue',
								handler: function(btn) {
									btn.up('window').hide();
								}
							}]
						}]
					});
				});
			}
			win.show();
		},
		updateUseStatus: function(id, cs, cr) {
			Ext.Ajax.request({
				url: basePath + 'fa/fix/assetscard/updateusestatus.action',
				params: {
					id: id,
					usestatus: cs
				},
				callback: function(opt, s, r) {
					var rs = Ext.decode(r.responseText);
					if(rs.exceptionInfo) {
						showError(rs.exceptionInfo);
					} else {
						Ext.Msg.alert("提示","更新成功！");
						window.location.reload();
					}
				}
			});
		},
		getComboData: function(callback) {
	 		var me = this;
	 		Ext.Ajax.request({
	    		url : basePath + 'common/getFieldsDatas.action',
	       		async: false,
	       		params: {
	       			caller: 'DataListCombo',
	       			fields: 'dlc_value,dlc_display',
	 				condition: 'dlc_caller=\'AssetsCard\' AND dlc_fieldname=\'ac_usestatus\' order by dlc_detno'
	       		},
	       		method : 'post',
	       		callback : function(options,success,response){
	       			var rs = new Ext.decode(response.responseText);
	       			if(rs.exceptionInfo){
	       				showError(rs.exceptionInfo);return null;
	       			}
	    			if(rs.success && rs.data){
	    				var data = Ext.decode(rs.data), arr = new Array();
	 	  				for(var i in data) {
	 	  					arr.push({
	 	  						display: data[i].DLC_VALUE,
	    						value: data[i].DLC_DISPLAY
	 	  					});
	 	  				}
	    				callback.call(me, arr);
	    			}
	       		}
	    	});
	 	}
	});