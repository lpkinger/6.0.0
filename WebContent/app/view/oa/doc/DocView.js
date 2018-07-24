Ext.define('erp.view.oa.doc.DocView', {
	extend: 'Ext.Panel',
	alias: 'widget.docview',
	id:'images-view',
	gridData:null,
	frame: true,
	autoScroll:true,
	height:window.innerHeight*0.52,
	columns:4,
	initComponent: function(){
		var data=new Array();
		if(this.gridData){
			Ext.Array.each(this.gridData.items,function(item){
			var style=item.data.dl_style.toLowerCase();
	        console.log("style: "+style);
			if('bmp,jpg,png,tiff,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,wmf'.indexOf(style)>= 0) 
				item.data.src='jsps/oa/doc/resources/images/images/docico/image.png">' ;
			else if('doc,docx,wps'.indexOf(style)>= 0)
				item.data.src='jsps/oa/doc/resources/images/images/docico/word.png">' ;
			else if('mp3,wma,rm,wav,midi,ape,flac'.indexOf(style)>= 0)
				item.data.src='jsps/oa/doc/resources/images/images/docico/music.png">' ;
			else if('xls,xlsx,xlsb,et,eet,xlt,xlsm'.indexOf(style)>= 0)
				item.data.src='jsps/oa/doc/resources/images/images/docico/excel.png">' ;
			else if('text,pdf'.indexOf(style)>= 0)
				item.data.src='jsps/oa/doc/resources/images/images/docico/'+style+'.png">' ;
			else if('ppt,pptx,dps'.indexOf(style)>= 0)
				item.data.src='jsps/oa/doc/resources/images/images/docico/ppt.png">' ;
			else if('zip,rar'.indexOf(style)>= 0)
				item.data.src='jsps/oa/doc/resources/images/images/docico/rar.png">' ;
			else if('zip,rar'.indexOf(style)>= 0)
					item.data.src='jsps/oa/doc/resources/images/images/docico/rar.png">' ;
			else   	item.data.src='jsps/oa/doc/resources/images/images/docico/others.png">';
		
				data.push(item.data);
			});
		}	  
		var store=Ext.create('Ext.data.Store', {
			id:'imgstore',
			data:data,
			fields: [{
				name: 'dl_id',
				type: 'number'
			},{
				name:'dl_name',
				type:'string'
			},{
				name: 'dl_locked',
				type: 'number'
			},{
				name:'dl_style',
				type:'string'
			},{
				name:'dl_size',
				type:'float'
			},{
				name:'dl_createtime',
				type:'date'
			},{
				name:'dl_creator',
				type:'string'
			},{
				name:'dl_kind',
				type:'number'
			},{
				name:'src',
				type:'string'
			}],
			sorters: [{
				property : 'dl_createtime',
				direction: 'DESC'
			}]
		});
		this.items=Ext.create('Ext.view.View', {
			store: Ext.data.StoreManager.lookup('imgstore'),
			/*tpl: [
			      '<tpl for=".">',
			      '<div><div><div style="position: absolute; left: 0; top: 0"><input type="checkbox" name="ids" /></div>',
			      '<div class="thumb-wrap" id="{dl_name}">',
			      '<div class="thumb"><img src="{src}" title="{dl_name}"></div>',
			      '<span class="x-editable">{dl_name}</span></div></div>',
			      '</tpl>',
			      '<div class="x-clear"></div></div>'
			      ],*/
			tpl:  ['<tpl for=".">',
	              '<div style="margin-bottom: 10px;" class="thumb-wrap">',
	               '<input  style="position: absolute;"  type="checkbox" name="ids" />',
	               '<img src="{src}" />',
	               '<br/><span>{dl_name}</span>',
	               '</div>',
	              '</tpl>'],
			      trackOver: true,
			      itemSelector: 'div.thumb-wrap',
			      emptyText: 'No images available',
			      plugins: [ Ext.create('Ext.ux.DataView.DragSelector', {}),
			                Ext.create('Ext.ux.DataView.LabelEditor', {dataIndex: 'dl_name'})]
		});     	
		this.callParent(arguments);
	}
});

