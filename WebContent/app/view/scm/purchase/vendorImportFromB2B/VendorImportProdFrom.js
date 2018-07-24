Ext.define('erp.view.scm.purchase.vendorImportFromB2B.VendorImportProdFrom',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpVendorImportProdFormPanel',
	id:'erpVendorImportProdFormPanel',
	region: 'north',
	tempStore:false,
    detailkeyfield:'',
    frame : true,
    layout : 'column',
	padding: '0 4 0 4',
	autoScroll : true,
	labelSeparator : ':',
	buttonAlign : 'center',
	enMsg:"",
	enName:"",
	enUU:0,
	searchcondition:{},
	searchInit:true,
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
    defaultType: 'textfield',
    defaults: {
    	width:210,
        labelWidth:70,
        cls:"x-vendor-searchline"
    },
	initComponent : function(){ 
		var me = this;
		me.tabid = getUrlParam("tabid");
		me.BaseUtil = Ext.create('erp.util.BaseUtil');
		if(parent.Ext.getCmp(me.tabid)){
			me.enMsg = parent.Ext.getCmp(me.tabid).detaildatas;
			me.enUU = me.enMsg.en_uu;
			me.searchcondition = parent.Ext.getCmp(me.tabid).searchcondition;
		}
		me.title = "<div style=\" display: inline-block; position: relative; top: -3px; left: 10px;\"><b>企业名：</b>"+me.enMsg.en_name+"</div>";
		if(me.enMsg&&me.enMsg.isVendor==0){
			me.title += "<div id=\"vendorImportBtn\" class=\"x-btn x-btn-gray x-vendor-import x-box-item x-toolbar-item x-btn-default-toolbar-small x-icon-text-left x-btn-icon-text-left x-btn-default-toolbar-small-icon-text-left\" ><em id=\"import-btnWrap\" class=\"\"><button id=\"import-btnEl\" type=\"button\" hidefocus=\"true\" role=\"button\" autocomplete=\"off\" class=\"x-btn-center\" onclick = importVendor('"+me.enMsg.en_uu+"','"+me.tabid+"')><span id=\"import-btnInnerEl\" class=\"x-btn-inner\" style=\"\">引进企业</span><span id=\"import-btnIconEl\" class=\"x-btn-icon x-button-icon-import\">&nbsp;</span></button></em></div>";
		}
		me.items = [{
		        	id:'pr_title',
		        	name: 'pr_title',
		        	fieldLabel: '物料名称'
		        },{
		            fieldLabel: '规格',
		            id:'pr_spec',
		        	name: 'pr_spec'
		        }/*,{
		            fieldLabel: '品牌',
		            id:'pr_brand',
		        	name: 'pr_brand'
		        }*/,{
			        id:'pr_brand',
			    	name: 'pr_brand',
			    	xtype: 'combo',
			    	displayField:'brand',
			    	valueField:'brand',
			    	hideTrigger:true,
		    		triggerCls: 'x-form-search-trigger',
			    	fieldLabel:'品牌',
			    	defaultListConfig:{
		               loadMask:false
		            },
			    	listeners:{
			    		select: function(combo, records) {
			    			combo.store.loading = false
			    			combo.select(records, true);
		    			},
						'change': function(cb, newValue, oldValue, e){
							var condition = " 1=1 ";
							if(newValue){
								condition = ' lower(nvl(pr_brand,\' \')) like \'%' + newValue + '%\' and pr_brand is not null ';
							}
							var form = Ext.getCmp('erpVendorImportProdFormPanel');
								form.getVendorFormB2B(cb,condition,'pr_brand',form.enUU);
						}
					}
	    		},{
		            fieldLabel: '原厂型号',
		            id:'pr_cmpcode',
		        	name: 'pr_cmpcode',
		        	xtype: 'combo',
			    	hideTrigger:true,
			    	fieldLabel:'型号',
			    	displayField:'cmpCode',
			    	valueField:'cmpCode',
			    	defaultListConfig:{
		               loadMask:false
		            },
			    	listeners:{
			    		select: function(combo, records) {
			    			combo.store.loading = false
			    			combo.select(records, true);
		    			},
						'change': function(cb, newValue, oldValue, e){
							var condition = " 1=1 ";
							if(newValue){
								var pr_brand = Ext.getCmp('pr_brand').getRawValue();
								condition = ' lower(nvl(pr_cmpcode,\' \')) like \'%' + newValue + '%\' and pr_cmpcode is not null';
								if(pr_brand){
									condition +=' and lower(nvl(pr_brand,\' \')) like \'%' +pr_brand+'%\' and pr_brand is not null' ; 
								}
							}
							var form = Ext.getCmp('erpVendorImportProdFormPanel');
		                    	form.getVendorFormB2B(cb,condition,'pr_cmpcode',form.enUU);
						}
					}
		        },{
		            fieldLabel: '类目',
		            id:'pr_kind',
		        	name: 'pr_kind'
		        },{
			    	xtype : 'button',
			    	name: 'query',
					id: 'query',
					text : "搜索",
					width:60,
					padding:'2 4 3 4',
					cls : 'x-btn-gray x-vendor-psearch',
					iconCls:'x-button-icon-query ',
					listeners:{
						'click':function(btn){
							var grid = Ext.getCmp('erpVendorImportProdGridPanel');
							grid.getStore().loadPage(1);
						}
					}
		        }
		];
		this.callParent(arguments);
	},
	getCondition :function(){
		var condition = '';
		var form = Ext.getCmp('erpVendorImportProdFormPanel');
		if(form){
			Ext.Array.each(form.items.items, function(item){
				if(item.xtype=='combo'&&item.getRawValue()&&item.getRawValue()!=null&&item.getRawValue()!=''){
					condition += item.name+" like '%"+item.getRawValue()+"%' and ";
				}
				if((item.xtype=='textfield' || item.xtype=='dbfindtrigger')&&item.value&&item.value!=null&&item.value!=''){
					condition += item.name+" like '%"+item.value+"%' and ";
				}
			});
		}
		if(condition=='') condition = ' 1=1 ';
		else condition = condition.substring(0,condition.length-4);
		return condition;
	},setDefaultValue :function(item,value){
		var defaultItem = Ext.getCmp(item);
		if(defaultItem){
			defaultItem.setValue(value);
		}
	},getVendorFormB2B : function(cb,condition,field,enUU){
		cb.collapse();
		Ext.defer(function(){
			Ext.Ajax.request({
		   		url : basePath + 'scm/purchase/getVendorFormB2B.action',
		   		async: false,//同步ajax请求
		   		params: {
		   			caller: caller,//如果table==null，则根据caller去form表取对应table
		   			condition: condition.toLowerCase()+" and pr_enuu = "+enUU,
		   			field: field,
		   			enUU:enUU
		   		},
		   		method : 'get',
		   		callback : function(options,success,response){
		   			if(response&&response.responseText){
		   				var localJson = new Ext.decode(response.responseText);//Ext.decode():解码(解析)json字符串对象
			   			if(localJson.exceptionInfo){
			   				showError(localJson.exceptionInfo);
			   			}
			   			if(localJson.data){
			   				cb.store.loadData(localJson.data,false);
			   				if(localJson.data.length>0){
			   					cb.expand();
			   				}else{
			   					cb.collapse();
			   				}
			   			}	
		   			}
		   		}
			});
		},200);
	}
	
});