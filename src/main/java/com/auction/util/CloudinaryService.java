package com.auction.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.File;
import java.util.Map;

public class CloudinaryService {

    // 1. Biến 'cloudinary' phải là static
    private static final Cloudinary cloudinary;

    // 2. KHOỐI STATIC (Static Initializer Block)
    // Khối này tự động chạy DUY NHẤT 1 LẦN khi class CloudinaryService được nạp vào bộ nhớ
    static {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "kurylrtx",
                "api_key",    "596944531336566",
                "api_secret", "2yFIJ8wSwNFjQej6rao4-mDhla8"
        ));
    }

    // Private constructor để ngăn không cho ai 'new' class này ở nơi khác
    private CloudinaryService() {}

    // 3. Hàm uploadImage bây giờ là STATIC!
    public static String uploadImage(File file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}