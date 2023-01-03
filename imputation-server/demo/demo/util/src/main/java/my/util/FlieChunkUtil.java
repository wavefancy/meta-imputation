//package my.util;
//
//public class FlieChunkUtil {
//	/**
//     * 分块上传
//     * 第一步：获取RandomAccessFile,随机访问文件类的对象
//     * 第二步：调用RandomAccessFile的getChannel()方法，打开文件通道 FileChannel
//     * 第三步：获取当前是第几个分块，计算文件的最后偏移量
//     * 第四步：获取当前文件分块的字节数组，用于获取文件字节长度
//     * 第五步：使用文件通道FileChannel类的 map（）方法创建直接字节缓冲器  MappedByteBuffer
//     * 第六步：将分块的字节数组放入到当前位置的缓冲区内  mappedByteBuffer.put(byte[] b);
//     * 第七步：释放缓冲区
//     * 第八步：检查文件是否全部完成上传
//     *
//     * @param param
//     * @return
//     * @throws Exception
//     */
//    public static ApiResult uploadByMappedByteBuffer(MultipartFileParam param) throws Exception {
//        if (param.getIdentifier() == null || "".equals(param.getIdentifier())) {
//            param.setIdentifier(UUID.randomUUID().toString());
//        }
//        // 判断是否上传
//        if (ObjectUtil.isEmpty(param.getFile())) {
//            return checkUploadStatus(param);
//        }
//        // 文件名称
//        String fileName = getFileName(param);
//        // 临时文件名称
//        String tempFileName = param.getIdentifier() + fileName.substring(fileName.lastIndexOf(".")) + "_tmp";
//        // 获取文件路径
//        String filePath = getUploadPath(param);
//        // 创建文件夹
//        FileUploadUtils.getAbsoluteFile(filePath, fileName);
//        // 创建临时文件
//        File tempFile = new File(filePath, tempFileName);
//        //第一步 获取RandomAccessFile,随机访问文件类的对象
//        RandomAccessFile raf = RandomAccessFileUitls.getModelRW(tempFile);
//        //第二步 调用RandomAccessFile的getChannel()方法，打开文件通道 FileChannel
//        FileChannel fileChannel = raf.getChannel();
//        //第三步 获取当前是第几个分块，计算文件的最后偏移量
//        long offset = (param.getChunkNumber() - 1) * param.getChunkSize();
//        //第四步 获取当前文件分块的字节数组，用于获取文件字节长度
//        byte[] fileData = param.getFile().getBytes();
//        //第五步 使用文件通道FileChannel类的 map（）方法创建直接字节缓冲器  MappedByteBuffer
//        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, fileData.length);
//        //第六步 将分块的字节数组放入到当前位置的缓冲区内  mappedByteBuffer.put(byte[] b)
//        mappedByteBuffer.put(fileData);
//        //第七步 释放缓冲区
//        freeMappedByteBuffer(mappedByteBuffer);
//        fileChannel.close();
//        raf.close();
//        //第八步 检查文件是否全部完成上传
//        ApiResult result = ApiResult.success();
//        boolean isComplete = checkUploadStatus(param, fileName, filePath);
//        if (isComplete) {
//            // 完成后，临时文件名为正式文件名
//            renameFile(tempFile, fileName);
//            result.put("endUpload", true);
//        }
//  
//        result.put("filePath", FileUploadUtils.getPathFileName(filePath, fileName));
//        result.put("fileName", param.getFile().getOriginalFilename());
//        return result;
//    }
//  
//    /**
//     * 检查文件是否上传
//     *
//     * @param param
//     * @return
//     * @throws Exception
//     */
//    public static ApiResult checkUploadStatus(MultipartFileParam param) throws Exception {
//        String fileName = getFileName(param);
//        // 校验conf文件
//        File confFile = checkConfFile(fileName, getUploadPath(param));
//        // 获取完成列表
//        byte[] completeStatusList = FileUtils.readFileToByteArray(confFile);
//        List<String> uploadeds = new ArrayList<>();
//        for (int i = 0; i < completeStatusList.length; i++) {
//            if (completeStatusList[i] == Byte.MAX_VALUE) {
//                uploadeds.add(i + 1 + "");
//            }
//        }
//        ApiResult<Void> success = ApiResult.success();
//        success.put("uploaded", uploadeds);
//        success.put("skipUpload", completeStatusList.length > 0 && completeStatusList.length == uploadeds.size());
//        // 新文件
//        if (ObjectUtil.isEmpty(completeStatusList)) {
//            success.put("chunk", false);
//            return success;
//        }
//        if (completeStatusList.length < param.getChunkNumber()) {
//            success.put("chunk", false);
//            return success;
//        }
//        byte b = completeStatusList[param.getChunkNumber() - 1];
//        if (b != Byte.MAX_VALUE) {
//            success.put("chunk", false);
//            return success;
//        }
//        success.put("filePath", FileUploadUtils.getPathFileName(getUploadPath(param), fileName));
//        success.put("chunk", true);
//        return success;
//    }
//  
//    /**
//     * 文件下载
//     *
//     * @param filePath 文件地址
//     * @param request
//     * @param response
//     * @throws IOException
//     */
//    public static void download(String filePath, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        // 初始化 response
//        response.reset();
//        // 获取文件
//        File file = new File(getDownloadPath(filePath));
//        long fileLength = file.length();
//        //获取从那个字节开始读取文件
//        String rangeString = request.getHeader("Range");
//        long range = 0;
//        if (StrUtil.isNotBlank(rangeString)) {
//            range = Long.valueOf(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
//        }
//        if (range >= fileLength) {
//            throw new CustomException("文件读取长度过长");
//        }
//        long byteLength = 1024 * 1024;
//        if (range + byteLength > fileLength) {
//            byteLength = fileLength;
//        }
//        // 随机读文件RandomAccessFile
//        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
//        try {
//            // 移动访问指针到指定位置
//            randomAccessFile.seek(range);
//            // 每次请求只返回1MB的视频流
//            byte[] bytes = new byte[(int) byteLength];
//            int len = randomAccessFile.read(bytes);
//            //获取响应的输出流
//            OutputStream outputStream = response.getOutputStream();
//            //返回码需要为206，代表只处理了部分请求，响应了部分数据
//            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
//            //设置此次相应返回的数据长度
//            response.setContentLength(len);
//            //设置此次相应返回的数据范围
//            response.setHeader("Content-Range", "bytes " + range + "-" + len + "/" + fileLength);
//            // 将这1MB的视频流响应给客户端
//            outputStream.write(bytes, 0, len);
//            outputStream.close();
//            //randomAccessFile.close();
//            System.out.println("返回数据区间:【" + range + "-" + (range + len) + "】");
//        } finally {
//            randomAccessFile.close();
//        }
//    }
//  
//    /**
//     * 文件重命名
//     *
//     * @param toBeRenamed   将要修改名字的文件
//     * @param toFileNewName 新的名字
//     * @return
//     */
//    private static boolean renameFile(File toBeRenamed, String toFileNewName) {
//        //检查要重命名的文件是否存在，是否是文件
//        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
//            return false;
//        }
//        String p = toBeRenamed.getParent();
//        File newFile = new File(p + File.separatorChar + toFileNewName);
//        //修改文件名
//        return toBeRenamed.renameTo(newFile);
//    }
//  
//    /**
//     * 检查文件上传进度
//     *
//     * @return
//     */
//    private static boolean checkUploadStatus(MultipartFileParam param, String fileName, String filePath) throws Exception {
//        // 校验conf文件
//        File confFile = checkConfFile(fileName, filePath);
//        // 读取conf
//        RandomAccessFile confAccessFile = new RandomAccessFile(confFile, "rw");
//        //设置文件长度
//        if (confAccessFile.length() != param.getTotalChunks()) {
//            confAccessFile.setLength(param.getTotalChunks());
//        }
//        //设置起始偏移量
//        confAccessFile.seek(param.getChunkNumber() - 1);
//        //将指定的一个字节写入文件中 127，
//        confAccessFile.write(Byte.MAX_VALUE);
//        byte[] completeStatusList = FileUtils.readFileToByteArray(confFile);
//        byte isComplete = Byte.MAX_VALUE;
//        //这一段逻辑有点复杂，看的时候思考了好久，创建conf文件文件长度为总分片数，每上传一个分块即向conf文件中写入一个127，那么没上传的位置就是默认的0,已上传的就是Byte.MAX_VALUE 127
//        for (int i = 0; i < completeStatusList.length && isComplete == Byte.MAX_VALUE; i++) {
//            // 按位与运算，将&两边的数转为二进制进行比较，有一个为0结果为0，全为1结果为1  eg.3&5  即 0000 0011 & 0000 0101 = 0000 0001   因此，3&5的值得1。
//            isComplete = (byte) (isComplete & completeStatusList[i]);
//        }
//        if (isComplete == Byte.MAX_VALUE) {
//            //如果全部文件上传完成，删除conf文件
//            // FileUtils.deleteFile(confFile.getPath());
//            return true;
//        }
//        return false;
//    }
//  
//  
//    /**
//     * 在MappedByteBuffer释放后再对它进行读操作的话就会引发jvm crash，在并发情况下很容易发生
//     * 正在释放时另一个线程正开始读取，于是crash就发生了。所以为了系统稳定性释放前一般需要检 查是否还有线程在读或写
//     *
//     * @param mappedByteBuffer
//     */
//    private static void freeMappedByteBuffer(final MappedByteBuffer mappedByteBuffer) {
//        try {
//            if (mappedByteBuffer == null) {
//                return;
//            }
//            mappedByteBuffer.force();
//            AccessController.doPrivileged(new PrivilegedAction<Object>() {
//                @Override
//                public Object run() {
//                    try {
//                        Method getCleanerMethod = mappedByteBuffer.getClass().getMethod("cleaner", new Class[0]);
//                        //可以访问private的权限
//                        getCleanerMethod.setAccessible(true);
//                        //在具有指定参数的 方法对象上调用此 方法对象表示的底层方法
//                        sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(mappedByteBuffer,
//                                new Object[0]);
//                        cleaner.clean();
//                    } catch (Exception e) {
//                        log.error("clean MappedByteBuffer error!!!", e);
//                    }
//                    return null;
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//  
//    private static String getFileName(MultipartFileParam param) {
//        String extension;
//        if (ObjectUtil.isNotEmpty(param.getFile())) {
//            // return param.getFile().getOriginalFilename();
//            String filename = param.getFile().getOriginalFilename();
//            extension = filename.substring(filename.lastIndexOf("."));
//            //return  FileUploadUtils.extractFilename(param.getFile());
//        } else {
//            extension = param.getFilename().substring(param.getFilename().lastIndexOf("."));
//            //return DateUtils.datePath() + "/" + IdUtil.fastUUID() + extension;
//        }
//        return param.getIdentifier() + extension;
//    }
//  
//    private static String getUploadPath(MultipartFileParam param) {
//        return FileUploadUtils.getDefaultBaseDir() + "/" + param.getObjectType();
//    }
//  
//    private static String getDownloadPath(String filePath) {
//        // 本地资源路径
//        String localPath = WhspConfig.getProfile();
//        // 数据库资源地址
//        String loadPath = localPath + StrUtil.subAfter(filePath, Constants.RESOURCE_PREFIX, false);
//        return loadPath;
//    }
//  
//    private static File checkConfFile(String fileName, String filePath) throws Exception {
//        File confFile = FileUploadUtils.getAbsoluteFile(filePath, fileName + ".conf");
//        if (!confFile.exists()) {
//            confFile.createNewFile();
//        }
//        return confFile;
//    }
//}
