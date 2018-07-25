Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.ModifyForm', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'core.form.Panel','pm.make.MakeCommon','core.grid.Panel2','core.toolbar.Toolbar','core.form.YnField',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Update',
  			'core.trigger.DbfindTrigger'
  		
  			],
    init:function(){
    	var me = this;
        me.FormUtil = Ext.create('erp.util.FormUtil');
        me.GridUtil = Ext.create('erp.util.GridUtil');
        me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'erpGridPanel2': { 
    			afterrender: function(grid){    				
    				grid.setReadOnly(true);
    			},
    			itemclick:me.ItemClick
    		},
    		'dbfindtrigger[name=mm_prodcode]': {
    			beforerender:function(trigger){
    				trigger.autoDbfind=false;
    			}
    			/*focus: function(t){
    				var grid = parent.Ext.getCmp('grid');
    				var c = null;
    				Ext.each(grid.store.data.items, function(item){
    					if(item.data['mm_prodcode'] != null && item.data['mm_prodcode'] != ''){
    						if(c == null){
        						c = "(pr_code<>'" + item.data['mm_prodcode'] + "'";
        					} else {
        						c += " and pr_code<>'" + item.data['mm_prodcode'] + "'";
        					}
    					}
    				});
    				if(c != null){
    					t.dbBaseCondition = c + ")";
    				}
    			}*/
    		},
    		'erpSaveButton' :{
    			click:function(btn){
    				//this.FormUtil.beforeSave(this);
    				var form=me.getForm(btn);
    				var r = form.getValues();
    				var ma_qty = parent.Ext.getCmp('ma_qty').value;
    				if(r.mm_qty<r.mm_oneuseqty*ma_qty){
	    				warnMsg('<font color="red">订单需求<单位用量*工单数</font></br>确定要保存?', function(btn, text){
	    				    if (btn == 'yes' || btn == 'ok'){
	    				    	me.save(form,r,me);
	    				    }else{
	    				    	return;
	    				    }
	    				});
    				}else{
    					me.save(form,r,me);
    				}
    			}    			
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				//me.GridUtil.deleteDetailForEditGrid(btn);
    				var grid = parent.Ext.getCmp('grid');
    				var records = grid.selModel.getSelection();
    				Ext.Ajax.request({
				   		url : basePath + "pm/make/deleteModifyMaterial.action",
				   		params: {
				   			id: records[0].data[grid.keyField]
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			grid.BaseUtil.getActiveTab().setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
			        			showError(localJson.exceptionInfo);return;
			        		}
			    			if(localJson.success){
			    				grid.store.remove(records[0]);
				   				delSuccess(function(){ 						
								});//@i18n/i18n.js
				   			 parent.Ext.getCmp('win').close();
				   			} else {
				   				delFailure();
				   			}
				   		}
					});
    			},    		
    		},
    		'erpCloseButton':{
    			click:function(btn){   			
    				parent.Ext.getCmp('win').close();
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    			  var form= me.getForm(btn);
    			  //form.getForm().reset(); 不行
    			  Ext.Array.each(form.items.items,function(item){
    				  if(item.name!='mm_maid'){
    				  item.setValue(null);  
    				  }
    			  });
    			}
    		},
    		'field[name=mm_oneuseqty]': {
    			change: function(f) {
    				var v = f.value || 0,
    					n = f.originalValue || 0,
    					q = parent.Ext.getCmp('ma_qty'),
    					ma_madeqty = parent.Ext.getCmp('ma_madeqty'),
    				    mm_id = Ext.getCmp('mm_id');
    				if(q){
    					qvalue = Number(q.value) || 0;
    				}else{
    					qvalue = 0;
    				}
    				if(ma_madeqty){
    					ma_madeqtyvalue = Number(ma_madeqty.value) || 0;
    				}else{
    					ma_madeqtyvalue = 0;
    				}
    				if(mm_id&&mm_id.value!=""&&mm_id.value!=null){
    					Ext.getCmp('mm_qty').setValue(n*ma_madeqtyvalue + v*(qvalue-ma_madeqtyvalue));
    				}else{
    					Ext.getCmp('mm_qty').setValue(v * (qvalue-ma_madeqtyvalue));
    				}
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	ItemClick:function(view ,record){
	 if(caller=='MakeBase!Sub'){
		 Ext.getCmp('deletebutton').setDisabled(false);
		var form=view.ownerCt.ownerCt.items.items[0];
		form.getForm().setValues(record.data);
	 }
	},
	save: function(form,r,me){
		var keys = Ext.Object.getKeys(r), f;
		Ext.each(keys, function(k){
			f = form.down('#' + k);
			if(f && f.logic == 'ignore') {
				delete r[k];
			}
		});
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params : {
	   			formStore: unescape(Ext.JSON.encode(r).replace(/\\/g,"%")),
	   			_noc:1
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = localJson.Id;
		   		    	var formCondition = "mm_id IS" + value ;   
		   		    	if(me.FormUtil.contains(window.location.href, '?', true)){
		   		    		var href=window.location.href;
			   		    	window.location.href = href.substring(0,href.lastIndexOf('&')) + '&whoami=&formCondition=' + 
			   		    	formCondition;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   		    	formCondition;
			   		    }
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					saveSuccess(function(){
	    					//add成功后刷新页面进入可编辑的页面 
			   				var value = r[form.keyField];
			   		    	var formCondition = form.keyField + "IS" + value ;
			   		    
			   		    	if(me.contains(window.location.href, '?', true)){
				   		    	window.location.href = window.location.href + '&formCondition=' + 
				   					formCondition;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   					formCondition;
				   		    }
	    				});
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		}
	   		
		});
	}
});