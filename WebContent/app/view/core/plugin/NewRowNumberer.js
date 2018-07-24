Ext.define("erp.view.core.plugin.NewRowNumberer", {
	extend : "Ext.grid.RowNumberer",
	alias: 'widget.newrownumberer',
	id:'newrownumberer',
	text:'<div>' +
	'<button id="search" class="newrownum_search" onclick="search()"></button>' +
	'</div>' +'<div>' +
	'<button id="clean" class="newrownum_clean" onclick="clean()"></button>' +
	'</div>',  
	textField:{
    	direct:'等于',
    	nodirect:'不等于',
    	head:'开头是',
    	end:'结尾是',
    	vague:'包含',
    	novague:'不包含',
    	'null':'',
    	'':'包含'
    },
    listeners:{
		'headerfilterschange':function(grid,filters,sure)
          	{
				var clean=document.getElementById("clean");
				var flag=false;
				if(sure==true){flag=true;}
		  	  	value=Ext.Object.getValues(filters);
		    	Ext.each(value,function(i){
		    		if(i!=''){
		    			flag=true;
		    			return;
		    		}   
		    	});
		    	clean.classList.remove(flag?"newrownum_uclean":"newrownum_clean");
				clean.classList.add(flag?"newrownum_clean":"newrownum_uclean");
			},
		'afterrender':function(){
			var caller = getUrlParam('whoami');
			if(!caller){
				caller = Scene;
			}
			this.hasFilterCondition(caller);
		}
        },
	initComponent:function(){
		this.callParent();	
	},
	hasFilterCondition:function(caller){
		Ext.Ajax.request({
		   url: basePath + 'common/Datalist/hasFilterCondition.action',	
		    params: {
				caller:caller
		    },
		   callback: function(opt, s, r) {
			   var rs = Ext.decode(r.responseText);
			   if(rs.exceptionInfo) {
				   showMessage('提示', rs.exceptionInfo);
				   return;
			   }else{
				   var search=document.getElementById("search");
				   search.classList.remove(rs.success?"newrownum_usearch":"newrownum_search");
        		   search.classList.add(rs.success?"newrownum_search":"newrownum_usearch");
			   } 							
		   }
	   });
	}
});
var search=function(){
	var whoami = getUrlParam('whoami');
	var urlcondition=getUrlParam('urlcondition');
	var numplugin = Ext.getCmp('newrownumberer');
	var grid=numplugin.ownerCt.ownerCt;
	var gridId = grid.id;
	if(!whoami){
		whoami = Scene;
	}
	Ext.create('Ext.window.Window', {
		title:'<div style="text-align:center">查询</div>',
		id:'searchwin',
		height:Ext.getBody().getHeight()*0.8,
		modal:true,
		draggable:true,
		resizable:false,
		closeAction:'destroy',
		width:850,
		items: {xtype: 'component',
			id:'iframe_detail',   					
			autoEl: {
				tag: 'iframe',
				style: 'height: 100%; width: 100%; border: none;',
				src: basePath + 'jsps/common/datalistFilter/datalistFilter.jsp?caller='+whoami+'&urlcondition='+urlcondition+'&gridId='+gridId
			}
		}
	}).show();	
}

var clean=function(){
	var clean=document.getElementById("clean");
	var search=document.getElementById("search");
	var flag=false;
	if(clean.classList.value!='newrownum_uclean'){
		var numplugin = Ext.getCmp('newrownumberer');
		var grid=numplugin.ownerCt.ownerCt;
		grid.filterCondition='';
		grid.defaultFilterCondition='';
		cleanValue(grid);
		grid.fromHeader=true;
		if(typeof(caller)=='undefined'){
			grid.getColumnsAndStore();
		}else{
			grid.getCount(caller,'','',true);
		}
		clean.classList.remove(flag?"newrownum_uclean":"newrownum_clean");
        clean.classList.add(flag?"newrownum_clean":"newrownum_uclean");
	}	
}
var cleanValue=function(grid){
	var columns = grid.columns;
	var numplugin = Ext.getCmp('newrownumberer');
	var res = new Ext.util.MixedCollection();
	for(var i in columns){
		var dataIndex = columns[i].dataIndex;
		if(dataIndex&&dataIndex != ""){
			var field = Ext.getCmp(dataIndex+'Filter');
			if(field.originalxtype == 'datefield'){
				field.fireEvent('resetBtn',function(){});
			}
			field.fromQuery = true;
			field.filterType = '';
			field.emptyText = '';
			field.inputEl.dom.placeholder = '';
			columns[i].textEl.dom.style.fontStyle='';
			columns[i].textEl.dom.style.fontWeight='';
			columns[i].textEl.dom.style.fontSize='14px';
			//columns[i].textEl.dom.style.textDecoration='';
			field.inputEl.dom.disabled="";
			field.inputEl.dom.style.background="#eee";
			if(field.originalxtype == 'combo' && field.getValue() != ""){
				field.isChange = true;
			}else if(field.originalxtype=='datefield'){
				field.resetValue(true);
			}
			field.setValue('');
			res.add(new Ext.util.Filter({
                property: dataIndex,
                value: '',
                root: 'data',
                label: columns[i].text||columns[i].header,
                type:field.originalxtype=='textfield'?numplugin.textField[field.filterType]:'',
                originalxtype:field.originalxtype,
                comboValue:field.originalxtype=='combo'?field.getRawValue():''
            }));
		}
	}
	grid.fireEvent('headerfilterchange',grid,res);
}

