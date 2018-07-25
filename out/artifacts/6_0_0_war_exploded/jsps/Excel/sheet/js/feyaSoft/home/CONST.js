Ext.ns('feyaSoft.home');

var ROOT_PATH = '';

feyaSoft.home.CONST = {
    BLANK_IMAGE_URL:'js/extjs/resources/images/default/s.gif',
    userLogo:'upload/user.gif',
    groupLogo:'upload/group.png',
    orgLogo: 'upload/dep64.png',
    companyLogo: 'images/icons/64px/company64.png',

    ROOT_PATH : '',
    
    // this is for the client definition
	//CLIENT_NAME : 'isi',
	//CLIENT_NAME : 'pradac',
	CLIENT_NAME : 'feyasoft',
    
    common: {
    	userAccess: basePath+ 'userAccess/isAccess'
    },
    
    documentUrl:{
    	editor: ROOT_PATH + 'editor',
    	spreadsheet: ROOT_PATH + 'spreadsheet',
    	presentation: ROOT_PATH + 'presentation',
    	listFiles: basePath + 'excel/getExcelTemplate.action',
    	listTree: ROOT_PATH + 'documentFile/listTree',
    	dragDrop: ROOT_PATH + 'documentFile/dragDrop',
    	createFolder: ROOT_PATH + 'documentFile/createFolder',
    	createFile: ROOT_PATH +'documentFile/createFile',
        update: ROOT_PATH +'documentFile/update',
        deleteFile: ROOT_PATH +'documentFile/delete',
        addRemove: ROOT_PATH +'secureDoc/addRemove',
        archive: ROOT_PATH +'documentFile/archive',
        getIdByAddress: ROOT_PATH +'documentFile/getIdByAddress',
        copyPaste: ROOT_PATH +'documentFile/copyPaste',
        loadPublicUrl: ROOT_PATH +'documentFile/loadPublicUrl',
        recover: ROOT_PATH +'documentFile/recover',
        ishaveCondition: basePath+'/excel/ishaveCondition.action',
        //isShield is check whether the file is shield, if yes, then need input username/password
        isShield: basePath +'/excel/isShield.action',
        loadFile: ROOT_PATH +'documentFile/loadFile',
        lockFile: ROOT_PATH +'documentFile/lockFile',
        unlockFile: ROOT_PATH +'documentFile/unlockFile',
        checkLockStatus: ROOT_PATH +'documentFile/checkLockStatus',
        uploadFile: ROOT_PATH +'documentFile/uploadFile',
        saveMyDoc: ROOT_PATH +'documentFile/saveMyDoc',
        exportFile: ROOT_PATH +'documentFile/exportFile'
    },
    
    spreadSheetUrl:{
	    loadTab : ROOT_PATH + 'spreadsheet/loadTabJson',
		loadPublicTab : ROOT_PATH + 'ssPublic/loadTabJson',
		sortRows :ROOT_PATH + 'spreadsheetTab/sortRows',
		updateBatchCells : ROOT_PATH + 'spreadsheetCell/updateBatchCells',
		newTab :basePath + 'sheetTab/insert.action',
		deleteTab :ROOT_PATH + 'spreadsheetTab/delete',
		renameTab :ROOT_PATH + 'spreadsheetTab/changeTabName',
		copyTab :ROOT_PATH + 'spreadsheetTab/copyPaste',
		extraInfo :ROOT_PATH + 'spreadsheetTab/setExtraInfo',
		fileExtraInfo :ROOT_PATH + 'spreadsheet/setExtraInfo',
		reorder :ROOT_PATH + 'spreadsheetTab/resetOrder',
		barChart:ROOT_PATH + 'barChart/index',
		pieChart:ROOT_PATH + 'pieChart/index',
		areaChart:ROOT_PATH + 'areaChart/index',
		lineChart:ROOT_PATH + 'xylineChart/index',
		scatterChart:ROOT_PATH + 'scatterChart/index',
		bubbleChart:ROOT_PATH + 'bubbleChart/index',
		tseriesChart:ROOT_PATH + 'tseriesChart/index',
		columnline:ROOT_PATH + 'columnline/index',
		createFileFromTemplate:basePath + 'excel/loadExcelTemplate.action',
		saveAs:ROOT_PATH + 'spreadsheet/saveAs',
		saveJsonAs:basePath + 'excel/saveJsonAs.action',
		loadJson:basePath + 'excel/loadExcelTemplate.action',
		loadPublicData:ROOT_PATH + 'ssPublic/loadPublicData',
		importExcelUpload:basePath + 'excel/uploadExcel.action?em_code=' + em_code,
		deleteTemplate:basePath+'excel/deleteTemplate.action',
		exportXlsx:ROOT_PATH + 'ssExportXlsx/export',
		exportExcel:ROOT_PATH + 'ssExportExcel/export',
		exportPDF:ROOT_PATH + 'ssConvertPDF/exportPDF',
		exportCSV:ROOT_PATH + 'ssExportExcel/exportCSV',
		restore:ROOT_PATH + 'spreadsheet/restore',
		listHistory : ROOT_PATH + 'spreadsheet/listHistory'
    },
    
    editorUrl:{
    	loadJson: ROOT_PATH + 'editor/loadJson',
    	loadPublicData: ROOT_PATH + 'wordPublic/loadPublicData',
    	exportPdfPrint: ROOT_PATH +'editorExport/print',
    	wordImportUpload: ROOT_PATH +'editorImport/upload',
    	saveEditor: ROOT_PATH +'editor/saveEditor',
    	exportWord: ROOT_PATH +'editorExport/exportDoc',
    	exportOdt: ROOT_PATH +'editorExport/exportOdt',
    	exportPdf: ROOT_PATH +'editorExport/exportPdf'
    },
    
    presentationUrl:{
    	loadPublicUrl: ROOT_PATH +'documentFile/loadPublicUrl',
    	loadFile: ROOT_PATH +'presentation/loadFile',
    	save: ROOT_PATH +'presentation/save',
    	saveAs: ROOT_PATH +'presentation/saveAs',
    	createUpdateSlide: ROOT_PATH + 'presentation/createUpdateSlide',
    	pasteSlide: ROOT_PATH +'presentation/pasteSlide',
    	deleteSlide: ROOT_PATH +'presentation/deleteSlide',
    	loadSlides: ROOT_PATH +'presentation/loadSlides',
    	changeOrder: ROOT_PATH +'presentation/changeOrder',
    	chanageMoveOrder: ROOT_PATH +'presentation/chanageMoveOrder',
    	changeBackground: ROOT_PATH +'presentation/changeBackground',
    	changeSlideFooter: ROOT_PATH +'presentation/changeSlideFooter',
    	checkFileName: ROOT_PATH +'presentation/checkFileName',
    	importUpload:ROOT_PATH + 'pptImport/upload',
    	pptSlideShow: ROOT_PATH +'pptSlideShow',
    	exportPPT: ROOT_PATH +'pptExportSlide/export',
    	printPdf: ROOT_PATH +'pptExportSlide/exportPdf',
    	exportPdf: ROOT_PATH +'pptExportPdf/export'  	
    },
    
    photoUrl:{
    	deletePhoto: ROOT_PATH +'photo/delete',
    	dragDrop: ROOT_PATH +'photoFolder/dragDrop',
    	dragDropToTree: ROOT_PATH +'photo/dragDropToTree',
    	resetOrderIn: ROOT_PATH +'photoFolder/resetOrderIn',
    	deleteFolder: ROOT_PATH +'photoFolder/delete',
    	createUpdateFolder: ROOT_PATH +'photoFolder/createUpdate',
    	createUpdatePhoto: ROOT_PATH +'photo/createUpdate',
    	myPhotoShow: ROOT_PATH +'myPhotoShow',
    	listPhoto: ROOT_PATH +'photo/list',
    	listTree: ROOT_PATH +'photoFolder/listTree'
    }
};