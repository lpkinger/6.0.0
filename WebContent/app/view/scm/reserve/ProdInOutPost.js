Ext.define('erp.view.scm.reserve.ProdInOutPost',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	},
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				width: 450,
				height: 220,
				bodyStyle: 'background: #f1f1f1;border:1px solid #cfcfcf',
				xtype: 'form',
				title: '批量过账',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				items: [{		
					margin : '15 0 0 0',
			    	xtype: 'condatefield',
			    	fieldLabel: '日期',
			    	allowBlank: false,
			    	readOnly: true,
			    	labelWidth: 80,
			    	id: 'date',
			    	name: 'date',
			    	width: 400
				},{
		 			margin: '5 0 0 5',
		 			xtype:'combo',
		 			fieldLabel:'单据类型',
		 			name:'pclass',
		 			id:'pclass',
		 			editable: false,
		 			displayField: 'display',
		 			valueField: 'value',
		 			queryMode: 'local',
		 			store : new Ext.data.Store({
		 				fields: ['display', 'value'],
		 				data: []
		 			}),
		 			labelWidth: 80,
		 			width: 400
		 		}],
				buttonAlign: 'center',
				bbar: {
					style:'border:1px solid #cfcfcf',
					items:['->',{
						xtype: 'erpConfirmButton',
						height: 26
					},{
						xtype:'erpCloseButton',
						height: 26
					},'->']
				}
			}] 
		});
		me.getComboData(caller, 'pclass', function(data){
    		me.down('#pclass').store.loadData(data);
    	});
		me.callParent(arguments); 
	},
	getComboData: function(table, field, callback) {
		var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		params: {
	   			caller: 'DataListCombo',
	   			fields: 'dlc_value,dlc_display',
	   			condition: 'dlc_caller=\'' + table + '\' AND dlc_fieldname=\'' + field + '\''
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);return;
	   			}
	  			if(localJson.success){
	  				var data = Ext.decode(localJson.data), arr = new Array();
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