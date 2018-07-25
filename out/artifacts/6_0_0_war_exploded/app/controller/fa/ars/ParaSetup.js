Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.ParaSetup', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.ars.ARBill','core.grid.Panel2','core.toolbar.Toolbar','core.toolbar.Toolbar2',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.ColorField'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.beforeSave();
    				
    			}
    		},
    		
    		'erpUpdateButton': {
    			click: function(btn){
    				me.beforeUpdate();    // 默认更新按钮是对 form操作,这里还是自己写方法吧。 
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addARBill', '新增应收发票单', 'jsps/fa/ars/arbill.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('ab_id').value);
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
   
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSave: function(){
		var me = this;
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
		var items = grid.store.data.items;
		var rowNo = [];
		Ext.each(items,function(item,index){
		
		Ext.each(grid.columns, function(c){
			if(item.data[c.dataIndex]!=item.raw[c.dataIndex])
				{
				if(!rowNo.contains(index+1)){
					
					rowNo.push(index+1);
				}
				}
			
			});
	 
		});
	
		if(rowNo.length==0){
			Ext.Msg.alert("你未对数据做任何修改!");
			return;
		}
		else{
			// confirm box modify
			// zhuth 2018-2-1
			Ext.Msg.confirm("提示", "第"+rowNo.toString()+"行已经修改,确定更新?", function(btn) {
				if(btn == 'yes') {
					var index = 0;
					var jsonGridData = new Array();
					var s = grid.getStore().data.items;
					for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
						var data = s[i].data;
						if(data['ps_fieldtype']=='B'){
							if(typeof(data['ps_value'])!='number'){
								Ext.Msg.alert("提示",'数据类型填入有误,请填入与第4列数据类型相符的数据！');
								return;
							}
						}
					}
					/*Ext.each(grid.columns, function(c){
						if(data['ps_fieldtype']=='B'){
							console.log(typeof(data['ps_value']));
							if(typeof(data['ps_value']!='number')){
								Ext.Msg.alert("提示",'第'+i+1+'行,第5列你必须填入boolean类型数值');
								return;
							}
						}
					if(data['ps_fieldtype']=='N'){
						if(typeof(data['ps_value']!='number')){
							Ext.Msg.alert("提示",'第'+i+1+'行,第5列你必须填入boolean类型数值');
							return;
						}
						
					} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
						if(data[c.dataIndex] == null || data[c.dataIndex] == ''){
							data[c.dataIndex] = '0';
						}
					}
				});*/
					jsonGridData[index++] = Ext.JSON.encode(data);
				}
				this.save(jsonGridData.toString());
			});
		}
	},
	save:function(param){
		Ext.Ajax.request({
		    url: basePath+'fa/ars/updateParaSetup.action',
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