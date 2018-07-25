var triggerfields=new Ext.form.TriggerField({  
	fieldLabel:'行业',
	labelWidth:70,
    name:'name', 
    id:'en_profession',
    editable:false,
    enableKeyEvents:true,
    mode : 'local',  
    triggerAction : 'all',
    trigger1Cls:'x-form-arrow-trigger', 
    trigger2Cls:'x-form-clear-trigger',    
    menu:new Ext.menu.Menu({  
	   id:'menus',
	   shadow:'frame', 
	   items:PROFESSION,
	   listeners:{
		   	beforeshow:function(me){
		   		var menuetrigger = document.getElementById('en_profession-bodyEl'),
				position = menuetrigger.getBoundingClientRect();
				me.x = position.x,
				me.y = position.y+22;
		   	}
	   }
	}),
	clearValue:function(){
		var en_profession = Ext.getCmp("en_profession");
   		en_profession.setValue(""); 
	},
    onSelect:function(record){ 
    },  
	onFocus:function(){
		this.onTrigger1Click();
	},
	onTrigger2Click: function() {
		this.clearValue(); 
		this.fireEvent('clear', this)
	},  
    onTrigger1Click:function(){ 
        if(this.menu==null)  
        this.menu=menu;  
        this.menu.show();  
    }
});
Ext.define('erp.view.scm.purchase.vendorImportFromB2B.VendorImportForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpVendorImportFormPanel',
	id:'erpVendorImportFormPanel',
	region: 'north',
	tempStore:false,
    detailkeyfield:'',
    frame : true,
    header: false,
	layout: 'anchor',
	padding: '0 4 0 4',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	defaults: {
    },
	items:[{
		xtype : 'fieldcontainer',
		id : 'vendorformSearch',
		name : 'vendorformSearch',
		layout : 'column',
		defaultType: 'textfield',
        defaults: {
        	cls:'searchBox searchBox-One',
        	width:240,
        	labelWidth:70
        },
        items: [{
		    	id:'en_name',
		    	name: 'en_name',
		    	fieldLabel:'企业名称'
		    },{
		    	id:'en_address',
		    	name: 'en_address',
		    	fieldLabel:'企业地址'
		    }/*,triggerfields*/,{
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
						Ext.getCmp('erpVendorImportFormPanel').getVendorFormB2B(cb,condition,'pr_brand');
					}
				}
	    },{
	        id:'pr_orispeccode',
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
					var condition = "1 = 1";
					if(newValue){
						var pr_brand = Ext.getCmp('pr_brand').getRawValue();
						condition = ' lower(nvl(pr_cmpcode,\' \')) like \'%' + newValue + '%\' and pr_cmpcode is not null ';
						if(pr_brand){
							condition +=' and lower(nvl(pr_brand,\' \')) like \'%' +pr_brand+'%\' and pr_brand is not null' ; 
						}
					}
					Ext.getCmp('erpVendorImportFormPanel').getVendorFormB2B(cb,condition,'pr_cmpcode');
				}
			}
	    }]
	  },{
		xtype : 'fieldcontainer',
		id : 'vendorProdSearch',
		name : 'vendorProdSearch',
		layout : 'column',
		defaultType: 'textfield',
        defaults: {
        	cls:'searchBox',
        	width:240,
        	labelWidth:70
        },
        items: [
        	{
	    	id:'pr_code',
	    	xtype: 'dbfindtrigger',
	    	triggerCls: 'x-form-search-trigger',
	    	name: 'pr_code',
	    	fieldLabel:'物料编号'
		},{
	        id:'pr_detail',
	    	name: 'pr_title',
	    	fieldLabel:'物料名称'
	    },{
	        id:'pr_spec',
	    	name: 'pr_spec',
	    	fieldLabel:'规格描述'
	    },{
	        id:'pr_kind',
	    	name: 'pr_kind',
	    	fieldLabel:'类目'
	    },{
	    	xtype : 'button',
	    	name: 'query',
			id: 'query',
			width:65,
			padding:'2 4 3 4',
			text : "搜索",
			cls : 'x-btn-gray x-vendor-import',
			iconCls:'x-button-icon-query ',
			listeners:{
				'click':function(btn){
					var grid = Ext.getCmp('erpVendorImportGridPanel');
					grid.getStore().loadPage(1);
				}
			}
		}]}],
	initComponent : function(){ 
		var me = this;
		me.callParent(arguments);
	},
	getSearchCondition :function(){
		var me = this;
		var vendorSearch = Ext.getCmp('vendorformSearch').items.items;
		var vendorProdSearch = Ext.getCmp('vendorProdSearch').items.items;
		var condition ="";
		var enterpriseMatchCondition ='"enterpriseMatchCondition":"'+me.conditionList(vendorSearch,"en_")+'"';
		var productMatchCondition ='"productMatchCondition":"'+me.conditionList(vendorProdSearch,"productMatch")+'"';
		condition = '{'+enterpriseMatchCondition+","+productMatchCondition+"}";
		return condition;
	},
	conditionList :function(array,type){
		var condition = '';
		var me = this;
		if(type==="en_"){
			Ext.Array.each(array, function(item){
				if(item.xtype=='textfield'&&item.value&&item.value!=null&&item.value!=''&&item.name.indexOf(type)>=0){
					condition += item.name+" like'%"+item.value+"%' and ";
				}
			});
			var en_profession = Ext.getCmp("en_profession")
			if(en_profession&&en_profession.value){
				condition+=" en_profession like '%"+en_profession.value+"%' and ";
			}
			condition = condition.substring(0,condition.length-4);
		}
		
		if(type === "productMatch"){
			var pr_brand = Ext.getCmp('pr_brand');
			var pr_cmpcode = Ext.getCmp('pr_orispeccode');
			if(pr_brand&&me.myTrim(pr_brand.getRawValue(),"g")!=""){//模糊匹配平台英文中文品牌名
				condition  = "and pr_brand like '%"+pr_brand.getRawValue()+"%'";
			}
			if(pr_cmpcode&&me.myTrim(pr_cmpcode.getRawValue(),"g")!=""){
				condition  += "and pr_cmpcode like '%"+pr_cmpcode.getRawValue()+"%'";
			}
			Ext.Array.each(array, function(item){
				if(item.xtype=='textfield'&&item.value&&item.value!=null&&item.value!=''){
					condition  += "and "+item.name+" like '%"+item.value+"%'";
				}
			});
			condition = condition.substring(3,condition.length);
		}
		if(condition=='') condition = ' 1=1 ';
		return condition;
	},showProfession:function(form){
		var pMenu = getProfession();
	},
	myTrim : function (str,is_global){
		var result = "";
		if(str){
			result = str.replace(/(^\s+)|(\s+$)/g,"");
	   		if(is_global.toLowerCase()=="g"){//全部空格
	   			result = result.replace(/\s/g,"");
	   		}
		}
   		return result;
	},
	getVendorFormB2B : function(cb,condition,field){
		cb.collapse();
		Ext.defer(function(){
			Ext.Ajax.request({
		   		url : basePath + 'scm/purchase/getVendorFormB2B.action',
		   		async: false,//同步ajax请求
		   		params: {
		   			caller: caller,//如果table==null，则根据caller去form表取对应table
		   			condition: condition.toLowerCase(),
		   			field: field
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