Ext.QuickTips.init();
Ext.define('erp.controller.fa.fundData.FundData', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.fundData.FundData','core.form.Panel','core.grid.Panel2','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
    		'core.button.ResSubmit','core.button.Update','core.button.Delete','core.button.ResAudit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.form.MonthDateField'
    	],
    init:function(){
    	var me = this;
    	this.control({
            'dbfindtrigger[name=fd_kind]': {
  			   aftertrigger: function(t){
  			   	var keyValue = Ext.getCmp('fd_id').value;
  			   	if (keyValue=='') {
  			   		me.autogetItems(keyValue,t.value);
  			   	}else{
				   	Ext.MessageBox.confirm('提示',
						'选择大类将会覆盖明细行数据',function(o){
						if (o=='yes') {
		  			   		me.autogetItems(keyValue,t.value);
						}else if(o=='no'){
							return;
						}
					});  			   	
  			   	}
  			   }
            },
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				this.FormUtil.beforeSave(me);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('FundData', '新增资金计划数据', 'jsps/fa/fundData/fundData.jsp?whoami=FundData');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('fd_id').value);
    			}
    		}
    		
    	});
    },
    autogetItems:function(id,kind){
    	var grid = Ext.getCmp('grid'),
    	store = grid.getStore();
    	Ext.Ajax.request({
			method: 'post',
            url : basePath + 'fa/fundData/autogetItems.action',
			async: false,
			params:{
				id:id,
				kind:kind
			},
            callback: function(options, success, response) {
            	var rs = new Ext.decode(response.responseText);
            	if (rs.success) {
            		var mydata = new Array();
            		Ext.Array.each(rs.data,function(item,index){
            			mydata.push({
            				fdd_id:null,
            				fdd_detno:index+1,
            				fdd_fdid:null,
            			    fdd_item:item.RD_ITEM,
            			    fdd_week1:null,
            			    fdd_week2:null,
            			    fdd_week3:null,
            			    fdd_week4:null,
            			    fdd_monthtotal:null
            			})
            		});
            		store.loadData(mydata);
            		//将grid所有数据设为dirty
            		var s = store.data.items;
            		for (var i=0;i<s.length;i++) {
            			s[i].dirty=true;
            			s[i].modified={fdd_item:'modify'}
            		}
            	}else if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
            }
		});    	
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});