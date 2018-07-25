Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.AccountParaSetup', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.grid.Panel4','core.toolbar.Toolbar3','core.button.Save','core.button.ResPost',
      		'core.trigger.DbfindTrigger','core.trigger.CateTreeDbfindTrigger'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel4': { 
    			itemclick: this.onGridItemClick,
    			afterrender: function(grid){
    				grid.plugins[0].on('beforeedit', function(e){
    					if(e.field == 'ps_value'){//监听ps_value
    						var record = e.record;
        					var column = e.column;
        					switch(record.data['ps_fieldtype']){
    					   		case "B"://boolean
    					   			if(column.xtype == 'booleancolumn'){
    					   				break;
    					   			}
    					   			column.setEditor(new Ext.form.field.ComboBox({
    					   				value: e.value,
    					   				store: Ext.create('Ext.data.Store', {
    					                    fields: ['display', 'value'],
    					                    data : [
    					                        {"display": $I18N.common.form.yes, "value": 'true'},
    					                        {"display": $I18N.common.form.no, "value": 'false'}
    					                    ]
    					                }),
    					                displayField: 'display',
    					                valueField: 'value',
    					        		queryMode: 'local',
    					        		hideTrigger: false
    					   			}));
    					   			column.renderer = function(val){
    					   				if(column.xtype == 'booleancolumn'){
    					   					if(val == 'true'){
    					   						return $I18N.common.form.yes;
    					   					} else {
    					   						return $I18N.common.form.no;
    					   					}
    					   				}
    					   				return val;    					   			
    					   			};
    					   			column.xtype = 'booleancolumn';
    					   			break;
    					   		case "N"://number
    					   			if(column.xtype == 'textcolumn'){
    					   				break;
    					   			}
    					   			column.setEditor(new Ext.form.field.Number({value: e.value}));
    					   			column.xtype = 'textcolumn';
    					   			break;
    					   		case "D"://date
    					   			column.renderer = function(val){
    					   				if(record.data['ps_fieldtype'] == 'D' && column.xtype == 'datecolumn'){
    					   					val = (val == null || val == '') ? new Date() : new Date(val);
        					   				val = Ext.Date.toString(val);
        					   				if(record.data['ps_value'] != val){
        					   					record.set('ps_value', val);
         					   				   

        					   				}
    					   				}
    					   				return val;    					   			
    					   			};
    					   			column.setEditor(new Ext.form.field.Date({value: e.value || new Date()}));
    					   			column.xtype = 'datecolumn';
//    					   			
    					   			break;
    					   		case "S"://string
    					   			if(column.xtype == 'textcolumn'){
    					   				break;
    					   			}
    					   			column.setEditor(new Ext.form.field.Text({value: e.value}));
    					   			column.xtype = 'textcolumn';
    					   			break;
    					   		case "F"://dbfind
//    					   			if(column.xtype == 'dbfindcolumn'){
//    					   				break;
//    					   			}
    					   			column.setEditor(new erp.view.core.trigger.CateTreeDbfindTrigger({value: e.value}));
    					   			column.xtype = 'catetreecolumn';
    					   			break;
    					   		case "C"://下拉框
    					   			if(column.xtype == 'combocolumn'){
    					   				break;
    					   			}
    					   			column.setEditor(new Ext.form.field.ComboBox({
    					   				displayField: 'display',
					   					valueField: 'value',
					   					queryMode: 'local',
    					   				store: Ext.create('Ext.data.Store', {
    					   					fields: ['display', 'value'],
    					   					data: [{
    					   						display: 'S-001-002',
    					   						value: 'S-001-002'
    					   					},{
    					   						display: 'S-001-003',
    					   						value: 'S-001-003'
    					   					}]
    					   				}),
    					   				value: e.value
    					   			}));
    					   			column.xtype = 'combocolumn';
    					   			break;
        					}
    					}
    				});
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.beforeUpdate();

    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    /*	this.gridLastSelected = record;
    	var grid = Ext.getCmp('grid');
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.GridUtil.onGridItemClick(selModel, record);*/
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSave: function(){
	
			this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		Array.prototype.contains = function(obj) {
		    var i = this.length;
		    while (i--) {
		        if (this[i] === obj) {
		            return true;
		        }
		    }
		    return false;
		};
	
		var grid = Ext.getCmp('grid');
//		var items = grid.store.data.items;
//		console.log(items);
//		var rowNo = [];
//		Ext.each(items,function(item,index){
//		
//		Ext.each(grid.columns, function(c){
//			if(item.data[c.dataIndex]!=item.raw[c.dataIndex])
//				{
//				if(!rowNo.contains(index+1)){
//					
//					rowNo.push(index+1);
//				}
//				}
//			
//			});
//	 
//		});
	
//		Ext.each(items,function(item,index){
//			if(item.dirty){
//				rowNo.push(index+1);
//			}
//			
//			
//		});
		
//		if(rowNo.length==0){
//			Ext.Msg.alert("你未对数据做任何修改!");
//			return;
//		}
//		else{
//			var result = confirm("第"+rowNo.toString()+"行已经修改,确定更新?");
//			if(result){
				var index = 0;
				var jsonGridData = new Array();
				var s = grid.getStore().data.items;
				for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
					var data = s[i].data;
					jsonGridData[index++] = Ext.JSON.encode(data);
				}
				this.update(jsonGridData.toString());
//			}
//			else return;
//		}
	
		
	
	
		
	},
	update:function(param){
		Ext.Ajax.request({
		    url: basePath+'fa/updateAccountParaSetup.action',
		    params:{
		    	param:param
		    },
		    success: function(response){
		        var text = response.responseText;
		        result = Ext.decode(text);
		        if(result.success){
		        	Ext.Msg.alert("保存成功！"); // 尚未国际化，以后订正。
		        }
		    }
		});
		
	}
		
});