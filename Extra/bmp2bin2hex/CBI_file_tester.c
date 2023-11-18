/**
 * @file CBI_file_tester.c
 * @brief Program to read and display data from custom binary image files.
 *
 * This program reads custom binary image files (with .cbi extension) from a specified directory,
 * processes the content, and displays the data of each image.
 *
 * It was designed solely as a means of testing python script that converts images into the custom binary file.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <dirent.h>
#include <sys/stat.h>
#include <stdbool.h>

#define FILE_EXT ".cbi"

/**
 * @brief Check if the given path is a directory.
 *
 * @param path Path to check.
 * @return True if the path is a directory, false otherwise.
 */
bool is_directory(const char *path) {
    struct stat statbuf;
    if (stat(path, &statbuf) != 0)
        return false;
    return S_ISDIR(statbuf.st_mode);
}

/**
 * @brief Find a binary file with the specified extension in a directory.
 *
 * @param directory Path to the directory to search in.
 * @param bin_file_path Pointer to store the path of the found file.
 * @return True if a file is found, false otherwise.
 */
bool find_bin_file(const char *directory, char **bin_file_path) {
    DIR *dir;
    struct dirent *ent;
    bool found = false;

    if ((dir = opendir(directory)) != NULL) {
        while ((ent = readdir(dir)) != NULL) {
            if (strstr(ent->d_name, FILE_EXT) != NULL) {
                size_t path_length = strlen(directory) + strlen(ent->d_name) + 2;
                *bin_file_path = malloc(path_length);
                if (*bin_file_path == NULL) {
                    fprintf(stderr, "Memory allocation failed\n");
                    exit(EXIT_FAILURE);
                }
                sprintf(*bin_file_path, "%s/%s", directory, ent->d_name);
                found = true;
                break;
            }
        }
        closedir(dir);
    }
    return found;
}

/**
 * @brief Process and print the content of a binary file.
 *
 * @param filepath Path to the binary file to be processed.
 */
void process_bin_file(const char *filepath) {
    FILE *file = fopen(filepath, "rb");
    if (file == NULL) {
        perror("Error opening file");
        return;
    }

    uint32_t num_images, width, height;
    if (fread(&num_images, sizeof(uint32_t), 1, file) != 1 ||
        fread(&width, sizeof(uint32_t), 1, file) != 1 ||
        fread(&height, sizeof(uint32_t), 1, file) != 1) {
        perror("Error reading header");
        fclose(file);
        return;
    }

    printf("Number of images: %u, Width: %u, Height: %u\n", num_images, width, height);

    uint32_t image_size = width * height / 8;
    uint8_t *image_data = malloc(image_size);
    if (image_data == NULL) {
        perror("Memory allocation failed");
        fclose(file);
        return;
    }

    // Reading and printing each image's data.
    for (uint32_t i = 0; i < num_images; ++i) {
        if (fread(image_data, sizeof(uint8_t), image_size, file) != image_size) {
            perror("Error reading image data");
            break;
        }

        printf("Image %u data: ", i + 1);
        for (uint32_t j = 0; j < image_size; ++j) {
            printf("%02x ", image_data[j]);
        }
        printf("\n");
    }

    free(image_data);
    fclose(file);
}

/**
 * @brief Main function to process command-line arguments and invoke file processing.
 *
 * @param argc Argument count.
 * @param argv Argument vector.
 * @return Exit status.
 */
int main(int argc, char *argv[]) {
    if (argc < 2) {
        printf("Usage: %s <directory_path>\n", argv[0]);
        return 1;
    }

    if (!is_directory(argv[1])) {
        printf("Error: %s is not a directory.\n", argv[1]);
        return 1;
    }

    char *bin_file_path = NULL;
    if (!find_bin_file(argv[1], &bin_file_path)) {
        printf("Error: No ${FILE_EXT} file found in the directory.\n");
        return 1;
    }

    process_bin_file(bin_file_path);

    return 0;
}
